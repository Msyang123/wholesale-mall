package com.lhiot.mall.wholesale.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leon.microx.util.ImmutableMap;
import com.leon.microx.util.SnowflakeId;
import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.base.DataMergeUtils;
import com.lhiot.mall.wholesale.base.JacksonUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.goods.domain.GoodsStandard;
import com.lhiot.mall.wholesale.goods.service.GoodsStandardService;
import com.lhiot.mall.wholesale.order.domain.*;
import com.lhiot.mall.wholesale.order.domain.gridparam.OrderGridParam;
import com.lhiot.mall.wholesale.order.mapper.OrderMapper;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.pay.domain.RefundLog;
import com.lhiot.mall.wholesale.pay.hdsend.Abolish;
import com.lhiot.mall.wholesale.pay.hdsend.Warehouse;
import com.lhiot.mall.wholesale.pay.mapper.RefundLogMapper;
import com.lhiot.mall.wholesale.pay.service.PaymentLogService;
import com.lhiot.mall.wholesale.setting.domain.ParamConfig;
import com.lhiot.mall.wholesale.setting.service.SettingService;
import com.lhiot.mall.wholesale.user.domain.SalesUser;
import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.service.SalesUserService;
import com.lhiot.mall.wholesale.user.service.UserService;
import com.lhiot.mall.wholesale.user.wechat.PaymentProperties;
import com.lhiot.mall.wholesale.user.wechat.WeChatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;


@Slf4j
@Service
@Transactional
public class OrderService {
    private final OrderMapper orderMapper;

    private final RefundLogMapper refundLogMapper;

    private final UserService userService;

    private final WeChatUtil weChatUtil;

    private final PaymentLogService paymentLogService;

    private final SnowflakeId snowflakeId;

    private final Warehouse warehouse;

    private final SalesUserService salesUserService;

    private final SettingService settingService;

    private final GoodsStandardService goodsStandardService;

    private final RabbitTemplate rabbit;

    @Autowired
    public OrderService(OrderMapper orderMapper, RefundLogMapper refundLogMapper, UserService userService, PaymentLogService paymentLogService,
                        PaymentProperties paymentProperties, SnowflakeId snowflakeId, Warehouse warehouse,
                        SalesUserService salesUserService, SettingService settingService, GoodsStandardService goodsStandardService, RabbitTemplate rabbit) {
        this.orderMapper = orderMapper;
        this.refundLogMapper = refundLogMapper;
        this.userService = userService;
        this.weChatUtil = new WeChatUtil(paymentProperties);
        this.paymentLogService = paymentLogService;
        this.snowflakeId = snowflakeId;
        this.warehouse = warehouse;
        this.salesUserService = salesUserService;
        this.settingService = settingService;
        this.goodsStandardService = goodsStandardService;
        this.rabbit = rabbit;
    }

    public List<OrderDetail> searchOrders(OrderDetail orderDetail) {
        return orderMapper.searchOrders(orderDetail);
    }

    public List<OrderDetail> searchOrdersByOrderCodes(String[] orderCodes) {
        return orderMapper.searchOrdersByOrderCodes(Arrays.asList(orderCodes));
    }

    public List<OrderGoods> searchOrderGoods(long orderId) {
        return orderMapper.searchOrderGoods(orderId);
    }

    public String searchOutstandingAccountsOrder(String orderCode) {
        return orderMapper.searchOutstandingAccountsOrder(orderCode);
    }

    public OrderDetail searchOrder(String orderCode) {
        OrderDetail orderDetail = orderMapper.searchOrder(orderCode);
        //商品信息
        if(Objects.nonNull(orderDetail)){
            orderDetail.setOrderGoodsList(orderMapper.searchOrderGoods(orderDetail.getId()));
        }
        return orderDetail;
    }

    /*    public OrderDetail searchOrderById(long orderId){
            return orderMapper.searchOrderById(orderId);
        }*/
    public OrderDetail searchOrderById(long id) {
        OrderDetail orderDetail = orderMapper.select(id);
        //商品信息
        if(Objects.nonNull(orderDetail)) {
            orderDetail.setOrderGoodsList(orderMapper.searchOrderGoods(orderDetail.getId()));
        }
        return orderDetail;
    }

    public List<OrderDetail> searchAfterSaleOrder(OrderDetail orderDetail) {
        return orderMapper.searchAfterSaleOrders(orderDetail);
    }

    public int create(OrderDetail orderDetail) throws JsonProcessingException {
        SalesUserRelation salesUserRelation = new SalesUserRelation();
        salesUserRelation.setUserId(orderDetail.getUserId());
        salesUserRelation.setAuditStatus("agree");//通过审核
        SalesUserRelation salesUserRelationResult = salesUserService.searchSaleRelationship(salesUserRelation);
        if (Objects.nonNull(salesUserRelationResult)) {
            //设置订单业务员编码
            orderDetail.setSalesmanId(salesUserRelationResult.getSalesmanId());
        }
        orderDetail.setPayStatus("unpaid");
        //产生订单编码
        orderDetail.setOrderCode(snowflakeId.stringId());
        orderDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
        orderDetail.setOrderStatus("unpaid");//先都设置为待付款 货到付款订单不需要需要支付
        orderDetail.setAfterStatus("no");//售后订单默认为否
        ParamConfig paramConfig = settingService.searchConfigParam("afterSalePeriod");//售后周期
        //设置售后截止时间
        Date d = new Date(System.currentTimeMillis()+Integer.valueOf(paramConfig.getConfigParamValue())*24*60*60*1000);

        orderDetail.setAfterSaleTime(new Timestamp(d.getTime()));
        //mq设置三十分钟失效
        rabbit.convertAndSend("order-direct-exchange", "order-dlx-queue", JacksonUtils.toJson(orderDetail), message -> {
            message.getMessageProperties().setExpiration(String.valueOf(30 * 60 * 1000));
            return message;
        });

        //保存订单信息
        orderMapper.save(orderDetail);
        //将保存的订单id赋值到订单商品中

        orderDetail.getOrderGoodsList().forEach(item -> {
            item.setOrderId(orderDetail.getId());
            //查询商品进货价写入到订单商品中
            GoodsStandard goodsStandard = goodsStandardService.searchByGoodsId(item.getGoodsId());
            item.setPurchasePrice(goodsStandard.getPurchasePrice());//进货价
            item.setStandardWeight(goodsStandard.getWeight());//规格重量
            item.setPaymentTime(new Timestamp(System.currentTimeMillis()));
            item.setRefundStatus("no");//是否退货:yes-已退货  no-未退货
        });
        //发送订单创建广播
        rabbit.convertAndSend("order-created-event", "", JacksonUtils.toJson(orderDetail));
        return orderMapper.saveOrderGoods(orderDetail.getOrderGoodsList());
    }

    @Scheduled(cron="0 0/2 *  * * ?")
    public void orderStatusTask(){
        boolean isBuyTime = settingService.isBuyTime();
        if (!isBuyTime){
            OrderDetail order = new OrderDetail();
            order.setOrderStatus("undelivery");
            List<OrderDetail> orderList = orderMapper.unDeliveryOrders(order);
            for (OrderDetail item:orderList) {
                OrderDetail updateOrderDetail=new OrderDetail();
                updateOrderDetail.setOrderStatus("delivery");
                updateOrderDetail.setCurrentOrderStatus("undelivery");
                updateOrderDetail.setOrderCode(item.getOrderCode());
                orderMapper.updateOrderStatusByCode(updateOrderDetail);
            }
        }
    }

    /**
     * 取消未支付订单
     *
     * @param orderCode
     * @return
     */
    public int cancelUnpayOrder(String orderCode) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderCode(orderCode);
        orderDetail.setOrderStatus("failed");
        orderDetail.setCurrentOrderStatus("unpaid");
        return orderMapper.updateOrderStatusByCode(orderDetail);
    }

    /**
     * 今日，昨日，本周，上周，本月，上月，近多少天 业务员业绩数据统计
     * @param salesmanId
     * @param param
     * @return
     */
    public OrderParam salesCount(long salesmanId,String param){
        List<Long> userIds = new ArrayList();
        List<SalesUserRelation> salesUserRelations = salesUserService.salesUser(salesmanId);
        if (Objects.nonNull(salesUserRelations)){
            for (SalesUserRelation item : salesUserRelations ) {
                userIds.add(item.getUserId());
            }
        }
        return orderMapper.salesCount(ImmutableMap.of("userIds",userIds,"param",param));
    }

    /**
     * 修改订单状态
     *
     * @param orderDetail
     * @return
     */
    public int updateOrderStatus(OrderDetail orderDetail) {
        return orderMapper.updateOrderStatusByCode(orderDetail);
    }

    /**
     * 确认订单收货
     */
    public int receivedOrder(String orderCode) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderCode(orderCode);
        orderDetail.setOrderStatus("received");
        orderDetail.setReceiveTime(new Timestamp(System.currentTimeMillis()));
        return orderMapper.updateOrder(orderDetail);
    }

    /**
     * 依据订单码修改订单信息
     *
     * @param orderDetail
     * @return
     */
    public int updateOrder(OrderDetail orderDetail) {
        return orderMapper.updateOrder(orderDetail);
    }

    /**
     * 依据订单号修改订单信息
     *
     * @param orderDetail
     * @return
     */
    public int updateOrderById(OrderDetail orderDetail) {
        return orderMapper.updateOrderById(orderDetail);
    }


    /**
     * 取消已支付订单 需要调用仓库取消掉订单
     *
     * @param orderDetail
     * @return
     */
    public String cancelPayedOrder(OrderDetail orderDetail) {

        //查询支付日志
        PaymentLog paymentLog = paymentLogService.getPaymentLog(orderDetail.getOrderCode());
        switch (orderDetail.getSettlementType()) {
            //货到付款
            case "offline":
                //如果未支付直接取消掉订单就可以了 如果支付了直接退成余额
                if (Objects.nonNull(paymentLog)) {
                    User updateUser = new User();
                    updateUser.setId(orderDetail.getUserId());
                    updateUser.setBalance(paymentLog.getTotalFee());//需要退还的用户余额
                    userService.updateBalance(updateUser);

                    //写入退款记录  t_whs_refund_log
                    RefundLog refundLog = new RefundLog();
                    refundLog.setPaymentLogId(paymentLog.getId());
                    refundLog.setRefundFee(paymentLog.getTotalFee());
                    refundLog.setRefundReason("当天退款");
                    refundLog.setRefundTime(new Timestamp(System.currentTimeMillis()));
                    refundLog.setTransactionId(paymentLog.getTransactionId());
                    refundLog.setRefundType("balanceRefund");
                    refundLog.setUserId(orderDetail.getUserId());
                    refundLogMapper.insertRefundLog(refundLog);
                    PaymentLog updatePaymentLog = new PaymentLog();
                    updatePaymentLog.setOrderCode(orderDetail.getOrderCode());
                    updatePaymentLog.setRefundFee(paymentLog.getTotalFee());
                    paymentLogService.updatePaymentLog(updatePaymentLog);
                }
                break;
            //微信支付
            case "wechat":
                if (Objects.isNull(paymentLog)) {
                    return "未找到支付记录";
                }
                //退款 如果微信支付就微信退款
                String refundChatFee = weChatUtil.refund(paymentLog.getOrderCode(), paymentLog.getTotalFee());
                //检查为没有失败信息
                if (StringUtils.isNotBlank(refundChatFee)&&refundChatFee.indexOf("FAIL")==-1) {
                    //写入退款记录  t_whs_refund_log
                    RefundLog refundLog = new RefundLog();
                    refundLog.setPaymentLogId(paymentLog.getId());
                    refundLog.setRefundFee(paymentLog.getTotalFee());
                    refundLog.setRefundReason("当天退款");
                    refundLog.setRefundTime(new Timestamp(System.currentTimeMillis()));
                    refundLog.setTransactionId(paymentLog.getTransactionId());
                    refundLog.setRefundType("wechatRefund");
                    refundLog.setUserId(orderDetail.getUserId());
                    refundLogMapper.insertRefundLog(refundLog);
                    PaymentLog updatePaymentLog = new PaymentLog();
                    updatePaymentLog.setOrderCode(orderDetail.getOrderCode());
                    updatePaymentLog.setRefundFee(paymentLog.getTotalFee());
                    paymentLogService.updatePaymentLog(updatePaymentLog);
                } else {
                    return "微信退款失败，请联系客服";
                }
                break;
            //余额支付
            case "balance":

                if (Objects.isNull(paymentLog)) {
                    return  "未找到支付记录";
                }
                User updateUser = new User();
                updateUser.setId(orderDetail.getUserId());
                updateUser.setBalance(paymentLog.getTotalFee());//需要退还的用户余额
                userService.updateBalance(updateUser);
                //写入退款记录  t_whs_refund_log
                RefundLog refundLog = new RefundLog();
                refundLog.setPaymentLogId(paymentLog.getId());
                refundLog.setRefundFee(paymentLog.getTotalFee());
                refundLog.setRefundReason("当天退款");
                refundLog.setRefundTime(new Timestamp(System.currentTimeMillis()));
                refundLog.setTransactionId(paymentLog.getTransactionId());
                refundLog.setRefundType("balanceRefund");
                refundLog.setUserId(orderDetail.getUserId());
                refundLogMapper.insertRefundLog(refundLog);
                PaymentLog updatePaymentLog = new PaymentLog();
                updatePaymentLog.setOrderCode(orderDetail.getOrderCode());
                updatePaymentLog.setRefundFee(paymentLog.getTotalFee());
                paymentLogService.updatePaymentLog(updatePaymentLog);
                break;
            default:
                log.info("退款未找到类型");
                break;
        }
        //退货到总仓
        /**********批发单服务-作废**********************************/
        Abolish abolish = new Abolish();
        abolish.setId(orderDetail.getHdCode());
        abolish.setSrcCls("批发商城");
        abolish.setOper("退货管理员");
        String cancelResult = warehouse.abolish(abolish);
        log.info(cancelResult);
        //修改为已退货
        OrderDetail updateOrderDetail = new OrderDetail();
        updateOrderDetail.setOrderCode(orderDetail.getOrderCode());
        updateOrderDetail.setCurrentOrderStatus("undelivery");
        updateOrderDetail.setOrderStatus("refunded");
        updateOrderStatus(updateOrderDetail);
        return null;
    }

    /**
     * 根据规格id统计商品的售卖数量
     *
     * @param standardIds 规格id,逗号分割
     * @param degree      系数
     * @return
     */
    public List<SoldQuantity> statisticalSoldQuantity(List<Long> standardIds, int degree) {
        List<SoldQuantity> soldQuantities = orderMapper.soldQuantity(standardIds);
        for (SoldQuantity soldQuantity : soldQuantities) {
            int count = soldQuantity.getSoldQuantity();
            //默认设置商品为1份
            count = Objects.isNull(count) ? 1 : count;
            //乘以系数
            soldQuantity.setSoldQuantity(count * degree);
        }
        return soldQuantities;
    }

    /**
     * 后台管理系统--分页查询订单信息
     *
     * @param param
     * @return
     */
    public PageQueryObject pageQuery(OrderGridParam param) throws InvocationTargetException, IntrospectionException, InstantiationException, IllegalAccessException {
        String phone = param.getPhone();
        List<OrderGridResult> orderList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        List<PaymentLog> paymentLogList = new ArrayList<>();
        List<OrderGridResult> orderGridResults = new ArrayList<>();
        List<Long> userIds = new ArrayList<Long>();
        List<String> orderCodes = new ArrayList<String>();
        int count = 0;
        int page = param.getPage();
        int rows = param.getRows();
        //总记录数
        int totalPages = 0;
        //查询条件中有手机号码，查询userId集合和用户信息
        if (Objects.nonNull(phone)) {
            User userParam = new User();
            userParam.setPhone(phone);
            userList = userService.searchByPhoneOrName(userParam);
            if (userList != null && userList.size() > 0) {
                for (User user : userList) {
                    userIds.add(user.getId());
                }
                param.setUserIds(userIds);
            }
        }
        count = orderMapper.pageQueryCount(param);
        //起始行
        param.setStart((page - 1) * rows);
        //总记录数
        totalPages = (count % rows == 0 ? count / rows : count / rows + 1);
        if (totalPages < page) {
            page = 1;
            param.setPage(page);
            param.setStart(0);
        }
        orderList = orderMapper.pageQuery(param);
        //封装查询结果
        PageQueryObject result = new PageQueryObject();
        if (orderList != null && orderList.size() > 0) {
            //查询订单集合中去重的userId集合和订orderCode集合
            for (OrderGridResult order : orderList) {
                if (!userIds.contains(order.getUserId())) {//用户id去重
                    userIds.add(order.getUserId());
                }
                if (!orderCodes.contains(order.getOrderCode())) {
                    orderCodes.add(order.getOrderCode());
                }
            }
            if (userList != null && userList.size() > 0 && !CollectionUtils.isEmpty(orderCodes)) {
                //用户信息不为空(已查询)，orderCode集合不为空，查询支付信息
                paymentLogList = paymentLogService.getPaymentLogList(orderCodes);
            } else if (userList == null && !CollectionUtils.isEmpty(userIds) && !CollectionUtils.isEmpty(orderCodes)){
                //用户信息为空（未查询），userIds集合不为空，orderCode集合不为空，查询用户信息和支付信息
                userList = userService.search(userIds);
                paymentLogList = paymentLogService.getPaymentLogList(orderCodes);
            }
            //订单信息与用户信息合并(不管userList和paymentLogList是否为空都执行--可优化判空)
            List<OrderGridResult> orderGridResults1 = DataMergeUtils.dataMerge(orderList, userList, "userId", "id", OrderGridResult.class);
            //合并结果与支付信息合并
            orderGridResults = DataMergeUtils.dataMerge(orderGridResults1,paymentLogList,"orderCode","orderCode",OrderGridResult.class);
        }
        result.setPage(page);
        result.setRecords(rows);
        result.setTotal(totalPages);
        result.setRows(orderGridResults);//将查询记录放入返回参数中
        return result;
    }

    public OrderDetail order(OrderDetail orderDetail) {
        return orderMapper.order(orderDetail);
    }

    public List<OrderDetail> lateOrders(OrderParam orderParam) {
        return orderMapper.lateOrders(orderParam);
    }

    public OrderDetail lateOneOrder(long userId) {
        return orderMapper.lateOneOrder(userId);
    }

    public Integer lateOrdersFee(OrderParam orderParam) {
        return orderMapper.lateOrdersFee(orderParam);
    }



    /**
     * 后台管理--查询订单详情
     *
     * @return
     */
    public OrderDetail detail(Long id) {
        //订单详情信息
        OrderDetail orderDetail = orderMapper.searchOrderById(id);
        if (Objects.nonNull(orderDetail)) {
            //用户信息
            User user = userService.user(orderDetail.getUserId());
            if (Objects.nonNull(user)) {
                orderDetail.setShopName(user.getShopName());
                orderDetail.setUserName(user.getUserName());
                orderDetail.setPhone(user.getPhone());
                orderDetail.setAddressDetail(user.getAddressDetail());
                orderDetail.setDeliveryAddress(user.getAddressDetail());
            }
            //支付信息
            PaymentLog paymentLog = paymentLogService.getPaymentLog(orderDetail.getOrderCode());
            if (Objects.nonNull(paymentLog)) {
                orderDetail.setPaymentTime(paymentLog.getPaymentTime());
            }
            //业务员信息
            SalesUser salesUser = salesUserService.findById(orderDetail.getSalesmanId());
            if (Objects.nonNull(salesUser)) {
                orderDetail.setSalesmanName(salesUser.getSalesmanName());
            }
            //商品信息
            List<OrderGoods> orderGoods = orderMapper.searchOrderGoods(orderDetail.getId());
            if (Objects.nonNull(orderGoods)) {
                orderDetail.setOrderGoodsList(orderGoods);
            }
        }
        return orderDetail;
    }

    /**
     * 后台管理--查询订单金额之和
     *
     * @return
     */
    public OrderDetail countFee(String orderIds) {
        String[] orderId = orderIds.split(",");
        List<String> list = Arrays.asList(orderId);
        return orderMapper.countFee(list);
    }


    public boolean isExistsOrderByuserId(Long userId) {
        if (null != userId) {
            return orderMapper.isExistsOrderByuserId(userId) > 0;
        }
        return false;
    }

    public Map<String, Object> countPayAbleFeeByUserId(List<Long> userIds, String startTime, String endTime) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userIds", userIds);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        return orderMapper.countPayAbleFee(param);
    }

    //根据userId查询欠款总额
    public Map<String, Object> countOverDue(List<Long> shopIds) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userIds", shopIds);
        return orderMapper.countOverDue(param);
    }

    public OrderDetail userOrder(OrderParam orderParam) {
        return orderMapper.userOrder(orderParam);
    }

    public List<OrderGridResult> orderGridData(OrderGridParam param) {
        return orderMapper.pageQuery(param);
    }

    /**
     * 后台管理系统--导出订单信息
     * @return
     */
    public List<Map<String, Object>> exportData(OrderGridParam param){
        return orderMapper.exportData(param);
    }

    /**
     * 后台管理系统--导出订单商品信息
     * @return
     */
    public List<Map<String, Object>> exportDataOrderGoods(OrderGridParam param){
        return orderMapper.exportDataOrderGoods(param);
    }

    /**
     * 根据订单查询编码查询订单
     * @param orderCode
     * @return
     */
    public OrderDetail findOrderByCode(String orderCode){
    	return orderMapper.searchOrder(orderCode);
    }
    
    /**
     * 计算商品的折扣价格
     * @param totalFee 订单总金额
     * @param payableFee 应付金额
     * @param goodsPrice 商品的价格
     * @param quantity 购买数量
     * @return
     */
    public int discountPrice(int totalFee,int payableFee,int goodsPrice){
    	BigDecimal totalFeeBig = new BigDecimal(String.valueOf(totalFee));
    	BigDecimal payableFeeBig = new BigDecimal(String.valueOf(payableFee));
    	
    	BigDecimal goodsPriceBig = new BigDecimal(String.valueOf(goodsPrice));
    	
    	BigDecimal dividend = payableFeeBig.multiply(goodsPriceBig);
    	BigDecimal result = dividend.divide(totalFeeBig, BigDecimal.ROUND_DOWN);
    	return result.intValue();
    }
}
