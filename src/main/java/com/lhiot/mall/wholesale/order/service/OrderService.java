package com.lhiot.mall.wholesale.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leon.microx.common.exception.ServiceException;
import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.base.JacksonUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.goods.domain.Goods;
import com.lhiot.mall.wholesale.goods.domain.GoodsStandard;
import com.lhiot.mall.wholesale.goods.service.GoodsService;
import com.lhiot.mall.wholesale.goods.service.GoodsStandardService;
import com.lhiot.mall.wholesale.order.domain.*;
import com.lhiot.mall.wholesale.order.domain.gridparam.OrderGridParam;
import com.lhiot.mall.wholesale.order.mapper.OrderMapper;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.pay.hdsend.Abolish;
import com.lhiot.mall.wholesale.pay.hdsend.Inventory;
import com.lhiot.mall.wholesale.pay.hdsend.Warehouse;
import com.lhiot.mall.wholesale.pay.service.PaymentLogService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final UserService userService;

    private final WeChatUtil weChatUtil;

    private final PaymentLogService paymentLogService;

    private final SnowflakeId snowflakeId;

    private final Warehouse warehouse;

    private final SalesUserService salesUserService;

    private final GoodsService goodsService;

    private final GoodsStandardService goodsStandardService;

    private final RabbitTemplate rabbit;

    @Autowired
    public OrderService(OrderMapper orderMapper, UserService userService, PaymentLogService paymentLogService,
                        PaymentProperties paymentProperties, SnowflakeId snowflakeId,Warehouse warehouse,
                        SalesUserService salesUserService,GoodsService goodsService,GoodsStandardService goodsStandardService,RabbitTemplate rabbit) {
        this.orderMapper = orderMapper;
        this.userService = userService;
        this.weChatUtil=new WeChatUtil(paymentProperties);
        this.paymentLogService=paymentLogService;
        this.snowflakeId=snowflakeId;
        this.warehouse=warehouse;
        this.salesUserService=salesUserService;
        this.goodsService=goodsService;
        this.goodsStandardService=goodsStandardService;
        this.rabbit=rabbit;
    }

    public List<OrderDetail> searchOrders(OrderDetail orderDetail){
        return orderMapper.searchOrders(orderDetail);
    }

    public List<OrderDetail> searchOrdersByOrderCodes(String[] orderCodes){
        return orderMapper.searchOrdersByOrderCodes(Arrays.asList(orderCodes));
    }

    public List<OrderGoods> searchOrderGoods(long orderId){
        return orderMapper.searchOrderGoods(orderId);
    }

    public String searchOutstandingAccountsOrder(String orderCode){
        return orderMapper.searchOutstandingAccountsOrder(orderCode);
    }

    public OrderDetail searchOrder(String orderCode){
        return orderMapper.searchOrder(orderCode);
    }

/*    public OrderDetail searchOrderById(long orderId){
        return orderMapper.searchOrderById(orderId);
    }*/
    public OrderDetail searchOrderById(long id) {
      return  orderMapper.select(id);
    }

    public List<OrderDetail> searchAfterSaleOrder(OrderDetail orderDetail) {
        return orderMapper.searchAfterSaleOrders(orderDetail);
    }
    public int create(OrderDetail orderDetail) throws JsonProcessingException {
        SalesUserRelation salesUserRelation=new SalesUserRelation();
        salesUserRelation.setUserId(orderDetail.getUserId());
        salesUserRelation.setAuditStatus("agree");//通过审核
        SalesUserRelation salesUserRelationResult=salesUserService.searchSaleRelationship(salesUserRelation);
        if(Objects.nonNull(salesUserRelationResult)){
            //设置订单业务员编码
            orderDetail.setSalesmanId(salesUserRelationResult.getSalesmanId());
        }
        orderDetail.setPayStatus("unpaid");
        //产生订单编码
        orderDetail.setOrderCode(snowflakeId.stringId());
        orderDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));

        if(Objects.equals(orderDetail.getSettlementType(),"cod")){  //改为枚举
            orderDetail.setOrderStatus("undelivery");//待收货
            //FIXME 直接发送总仓
            Inventory inventory=new Inventory();
            inventory.setUuid(UUID.randomUUID().toString());
            inventory.setSenderCode("9646");
            inventory.setSenderWrh("07310101");
            inventory.setReceiverCode(null);
            inventory.setContactor("老曹");
            inventory.setPhoneNumber("18888888888");
            inventory.setDeliverAddress("五一大道98号");
            inventory.setRemark("快点送");
            inventory.setOcrDate(new Date());
            inventory.setFiller("填单人");
            inventory.setSeller("销售员");
            inventory.setSouceOrderCls("批发商城");
            inventory.setNegInvFlag("1");
            inventory.setMemberCode(null);
            inventory.setFreight(new BigDecimal(21.3));

            //清单
            List<Inventory.WholeSaleDtl> wholeSaleDtlList=new ArrayList<>();
            Inventory.WholeSaleDtl wholeSaleDtl1=inventory.new WholeSaleDtl();
            wholeSaleDtl1.setSkuId("010100100011");
            wholeSaleDtl1.setQty(new BigDecimal(3));
            wholeSaleDtl1.setPrice(new BigDecimal(100.1));
            wholeSaleDtl1.setTotal(null);
            wholeSaleDtl1.setFreight(null);
            wholeSaleDtl1.setPayAmount(new BigDecimal((99.1)));
            wholeSaleDtl1.setUnitPrice(null);
            wholeSaleDtl1.setPriceAmount(new BigDecimal(200.1));
            wholeSaleDtl1.setBuyAmount(new BigDecimal(99.2));
            wholeSaleDtl1.setBusinessDiscount(new BigDecimal(0.1));
            wholeSaleDtl1.setPlatformDiscount(new BigDecimal(0));
            wholeSaleDtl1.setQpc(new BigDecimal(5));
            wholeSaleDtl1.setQpcStr("1*5");

            wholeSaleDtlList.add(wholeSaleDtl1);
            inventory.setProducts(wholeSaleDtlList);

            List<Inventory.Pay> pays=new ArrayList<>();
            Inventory.Pay pay=inventory.new Pay();

            pay.setTotal(new BigDecimal(234.56));
            pay.setPayName("现金支付");
            pays.add(pay);
            inventory.setPays(pays);

            String hdCode=warehouse.savenew2state(inventory);
            log.info(hdCode);

            //修改订单状态为已支付状态
            orderDetail.setHdCode(hdCode);//总仓编码
            orderDetail.setHdStatus("success");//海鼎发送成功
            orderDetail.setOrderStatus("undelivery");//待发货状态

        }else{
            orderDetail.setOrderStatus("unpaid");//待付款
            //mq设置三十分钟失效
            rabbit.convertAndSend("order-direct-exchange", "order-dlx-queue", JacksonUtils.toJson(orderDetail), message -> {
                message.getMessageProperties().setExpiration(String.valueOf(1 * 60 * 1000));
                return message;
            });
        }


        orderMapper.save(orderDetail);
        //将保存的订单id赋值到订单商品中
        orderDetail.getOrderGoodsList().forEach(item->{
            item.setOrderId(orderDetail.getId());
            //查询商品进货价写入到订单商品中
            GoodsStandard goodsStandard= goodsStandardService.searchByGoodsId(item.getGoodsId());
            item.setPurchasePrice(goodsStandard.getPurchasePrice());
            //减商品库存
            Goods goods=new Goods();
            goods.setId(item.getGoodsId());
            goods.setReduceStockLimit(item.getQuanity());//递减
            goodsService.update(goods);
        });
        //发送订单创建广播
        //FIXME 创建的时候发送创建广播消息 用于优惠券设置无效
        rabbit.convertAndSend("order-created-event","",JacksonUtils.toJson(orderDetail));
        return orderMapper.saveOrderGoods(orderDetail.getOrderGoodsList());
    }

    /**
     * 取消未支付订单
     * @param orderCode
     * @return
     */
    public int cancelUnpayOrder(String orderCode){
        OrderDetail orderDetail=new OrderDetail();
        orderDetail.setOrderCode(orderCode);
        orderDetail.setOrderStatus("failed");
        orderDetail.setCurrentOrderStatus("unpaid");
        return orderMapper.updateOrderStatusByCode(orderDetail);
    }

    /**
     * 修改订单状态
     * @param orderDetail
     * @return
     */
    public int updateOrderStatus(OrderDetail orderDetail){
        return orderMapper.updateOrderStatusByCode(orderDetail);
    }

    /**
     * 修改订单状态
     * @param orderDetail
     * @return
     */
    public int updateOrder(OrderDetail orderDetail){
        return orderMapper.updateOrder(orderDetail);
    }

    /**
     * 取消已支付订单 需要调用仓库取消掉订单
     * @param orderDetail
     * @return
     */
    public int cancelPayedOrder(OrderDetail orderDetail){

       PaymentLog paymentLog= paymentLogService.getPaymentLog(orderDetail.getOrderCode());

       if(Objects.isNull(paymentLog)){
           throw new ServiceException("未找到支付记录");
       }
       //退货到总仓
        /**********批发单服务-作废**********************************/
        Abolish abolish=new Abolish();
        abolish.setId(orderDetail.getHdCode());
        abolish.setSrcCls("批发商城");
        abolish.setOper("退货管理员");
        String cancelResult=warehouse.abolish(abolish);
       log.info(cancelResult);

       //FIXME 查询支付日志

         switch (orderDetail.getSettlementType()) {
            //1货到付款
            case "cod":
                //直接取消掉订单就可以了
                break;
            //0 线上支付
            case "online":

                //退款 如果微信支付就微信退款
                try {
                    weChatUtil.refund(paymentLog.getTransactionId(), paymentLog.getTotalFee());

                    //TODO 写入退款记录  t_whs_refund_log
                } catch (Exception e) {
                    throw new ServiceException("微信退款失败，请联系客服");
                }
                break;
            default:
                break;
        }
        return 1;
    }

    /**
     * 根据规格id统计商品的售卖数量
     * @param standardIds 规格id,逗号分割
     * @param degree 系数
     * @return
     */
    public List<SoldQuantity> statisticalSoldQuantity(List<Long> standardIds,int degree){
    	List<SoldQuantity> soldQuantities = orderMapper.soldQuantity(standardIds);
    	for(SoldQuantity soldQuantity : soldQuantities){
    		int count = soldQuantity.getSoldQuantity();
    		//默认设置商品为1份
    		count = Objects.isNull(count) ? 1 : count;
    		//乘以系数
    		soldQuantity.setSoldQuantity(count*degree);
    	}
    	return soldQuantities;
    }

    /**
     * 后台管理系统--分页查询订单信息
     * @param param
     * @return
     */
    public PageQueryObject pageQuery(OrderGridParam param) throws InvocationTargetException, IntrospectionException, InstantiationException, IllegalAccessException {
        String phone = param.getPhone();
        User userParam = new User();
        userParam.setPhone(phone);
        List<OrderGridResult> orderGridResultList = new ArrayList<OrderGridResult>();
        List<User> userList = new ArrayList<User>();
        List<PaymentLog> paymentLogList = new ArrayList<PaymentLog>();
        int count = 0;
        int page = param.getPage();
        int rows = param.getRows();
        //总记录数
        int totalPages = 0;
        if(phone == null){//未传手机号查询条件,先根据条件查询分页的订单列表及用户ids，再根据ids查询用户信息列表
            count = orderMapper.pageQueryCount(param);
            //起始行
            param.setStart((page-1)*rows);
            //总记录数
            totalPages = (count%rows==0?count/rows:count/rows+1);
            if(totalPages < page){
                page = 1;
                param.setPage(page);
                param.setStart(0);
            }
            orderGridResultList = orderMapper.pageQuery(param);
            List<Long> userIds = new ArrayList<Long>();
            List<Long> orderIds = new ArrayList<Long>();
            if(orderGridResultList != null && orderGridResultList.size() > 0){//查询订单对应的用户ID列表与订单ID列表
                for(OrderGridResult orderGridResult : orderGridResultList){
                    long userId = orderGridResult.getUserId();
                    long orderId = orderGridResult.getId();
                    if(!userIds.contains(userId)){//用户id去重
                        userIds.add(userId);
                    }
                    if(!orderIds.contains(orderId)){
                        orderIds.add(orderId);
                    }
                }
            }
            userList = userService.search(userIds);//根据用户ID列表查询用户信息
            paymentLogList = paymentLogService.getPaymentLogList(orderIds);//根据订单ID列表查询支付信息
        }else{//传了手机号查询条件，先根据条件查询用户列表及用户ids，再根据ids和订单其他信息查询订单信息列表
            userList = userService.searchByPhoneOrName(userParam);
            List<Long> userIds = new ArrayList<Long>();
            if(userList != null && userList.size() > 0){
                for(User user : userList){
                    userIds.add(user.getId());
                }
                param.setUserIds(userIds);
                count = orderMapper.pageQueryCount(param);
                //起始行
                param.setStart((page-1)*rows);
                //总记录数
                totalPages = (count%rows==0?count/rows:count/rows+1);
                if(totalPages < page){
                    page = 1;
                    param.setPage(page);
                    param.setStart(0);
                }
                orderGridResultList = orderMapper.pageQuery(param);//根据用户ID列表及其他查询条件查询用户信息
                List<Long> orderIds = new ArrayList<Long>();
                if(orderGridResultList != null && orderGridResultList.size() > 0){
                    for(OrderGridResult orderGridResult : orderGridResultList){
                        orderIds.add(orderGridResult.getId());
                    }
                }
                paymentLogList = paymentLogService.getPaymentLogList(orderIds);//根据订单ID列表查询支付信息
            }
        }
        PageQueryObject result = new PageQueryObject();
        if(orderGridResultList != null && orderGridResultList.size() > 0){//如果订单信息不为空,将订单列表与用户信息列表进行行数据组装
            //根据用户id与订单中的用户id匹配
            for (OrderGridResult orderGridResult : orderGridResultList) {
                Long orderUserId = orderGridResult.getUserId();
                for (User user : userList) {
                    Long uId = user.getId();
                    if (Objects.equals(orderUserId, uId)) {
                        orderGridResult.setPhone(user.getPhone());
                        orderGridResult.setShopName(user.getShopName());
                        orderGridResult.setUserName(user.getUserName());
                        break;
                    }
                }
            }
            //根据订单id和支付记录orderId进行信息匹配
            for (OrderGridResult orderGridResult : orderGridResultList) {
                Long orderId = orderGridResult.getId();
                for (PaymentLog paymentLog : paymentLogList) {
                    Long pOrderId = paymentLog.getOrderId();
                    if (Objects.equals(orderId, pOrderId)) {
                        orderGridResult.setPaymentTime(paymentLog.getPaymentTime());
                        break;
                    }
                }
            }
        }
        result.setPage(page);
        result.setRecords(rows);
        result.setTotal(totalPages);
        result.setRows(orderGridResultList);//将查询记录放入返回参数中
        return result;
    }

    public OrderDetail order(OrderDetail orderDetail){
        return orderMapper.order(orderDetail);
    }

    public List<OrderDetail> lateOrders(OrderParam orderParam){
        return orderMapper.lateOrders(orderParam);
    }

    public OrderDetail lateOneOrder(long userId){
        return orderMapper.lateOneOrder(userId);
    }

    public Integer lateOrdersFee(OrderParam orderParam) {
        return orderMapper.lateOrdersFee(orderParam);
    }
    /**
     * 后台管理--查询订单详情
     * @return
     */
    public OrderDetail detail(Long id) {
        //账款订单详情信息
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

    public OrderDetail userOrder(OrderParam orderParam){
        return orderMapper.userOrder(orderParam);
    }

    public List<OrderGridResult> orderGridData(OrderGridParam param){
        return orderMapper.pageQuery(param);
    }

}
