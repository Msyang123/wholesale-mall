package com.lhiot.mall.wholesale.order.service;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.lhiot.mall.wholesale.order.domain.OrderGoods;
import com.lhiot.mall.wholesale.order.domain.OrderParam;
import com.lhiot.mall.wholesale.base.DataMergeUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.demand.domain.DemandGoods;
import com.lhiot.mall.wholesale.demand.domain.DemandGoodsResult;
import com.lhiot.mall.wholesale.goods.domain.Goods;
import com.lhiot.mall.wholesale.order.domain.*;
import com.lhiot.mall.wholesale.order.domain.gridparam.OrderGridParam;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.leon.microx.common.exception.ServiceException;
import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.order.mapper.OrderMapper;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.pay.service.PaymentLogService;
import com.lhiot.mall.wholesale.user.wechat.PaymentProperties;
import com.lhiot.mall.wholesale.user.wechat.WeChatUtil;
import com.sgsl.hd.client.HaiDingClient;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@Service
@Transactional
public class OrderService {
    private final OrderMapper orderMapper;

    private final UserService userService;

    private final HaiDingClient hdClient;

    private final WeChatUtil weChatUtil;

    private final PaymentLogService paymentLogService;

    private final SnowflakeId snowflakeId;

    @Autowired
    public OrderService(OrderMapper orderMapper, UserService userService, HaiDingClient hdClient, PaymentLogService paymentLogService,
                        PaymentProperties paymentProperties, SnowflakeId snowflakeId) {
        this.orderMapper = orderMapper;
        this.userService = userService;
        this.hdClient=hdClient;
        this.weChatUtil=new WeChatUtil(paymentProperties);
        this.paymentLogService=paymentLogService;
        this.snowflakeId=snowflakeId;
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

    public OrderDetail searchOrderById(long orderId){
        return orderMapper.searchOrderById(orderId);
    }

    public List<OrderDetail> searchAfterSaleOrder(OrderDetail orderDetail) {
        return orderMapper.searchAfterSaleOrders(orderDetail);
    }
    public int create(OrderDetail orderDetail){
        //产生订单编码
        orderDetail.setOrderCode(snowflakeId.stringId());
        orderDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
        orderMapper.save(orderDetail);
        //将保存的订单id赋值到订单商品中
        orderDetail.getOrderGoodsList().forEach(item->{
            item.setOrderId(orderDetail.getId());
        });
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
     * 取消已支付订单 需要调用仓库取消掉订单
     * @param orderDetail
     * @return
     */
    public int cancelPayedOrder(OrderDetail orderDetail){

       PaymentLog paymentLog= paymentLogService.getPaymentLog(orderDetail.getOrderCode());
       String cancelResult= hdClient.cancelOrder(orderDetail.getOrderCode(),"当天无条件退货");
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
                        orderGridResult.setCreateTime(orderGridResult.getCreateTime().toString());
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
        OrderDetail orderDetail = orderMapper.select(id);
        if (Objects.nonNull(orderDetail)) {
            User user = userService.user(orderDetail.getUserId()); //用户信息
            if (Objects.nonNull(user)) {
                orderDetail.setShopName(user.getShopName());
                orderDetail.setUserName(user.getUserName());
                orderDetail.setPhone(user.getPhone());
                orderDetail.setAddressDetail(user.getAddressDetail());
            }
        }
        return orderDetail;
    }

    public OrderDetail userOrder(OrderParam orderParam){
        return orderMapper.userOrder(orderParam);
    }
}
