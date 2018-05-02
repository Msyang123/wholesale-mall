package com.lhiot.mall.wholesale.order.service;


import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.lhiot.mall.wholesale.order.domain.OrderGoods;
import com.lhiot.mall.wholesale.order.domain.OrderParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.common.exception.ServiceException;
import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.SoldQuantity;
import com.lhiot.mall.wholesale.order.mapper.OrderMapper;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.pay.service.PaymentLogService;
import com.lhiot.mall.wholesale.user.mapper.UserMapper;
import com.lhiot.mall.wholesale.user.wechat.PaymentProperties;
import com.lhiot.mall.wholesale.user.wechat.WeChatUtil;
import com.sgsl.hd.client.HaiDingClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class OrderService {
    private final OrderMapper orderMapper;

    private final UserMapper userMapper;

    private final HaiDingClient hdClient;

    private final WeChatUtil weChatUtil;

    private final PaymentLogService paymentLogService;

    private final SnowflakeId snowflakeId;

    @Autowired
    public OrderService(OrderMapper orderMapper, HaiDingClient hdClient, PaymentLogService paymentLogService, 
    		PaymentProperties paymentProperties,SnowflakeId snowflakeId,
    		UserMapper userMapper) {
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
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

    public List<OrderDetail> searchAfterSaleOrder(OrderDetail orderDetail) {
        return orderMapper.searchAfterSaleOrders(orderDetail);
    }
    public int create(OrderDetail orderDetail){
        //产生订单编码
        orderDetail.setOrderCode(snowflakeId.stringId());
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
       /* orderDetail.setOrderStatus(0);
        orderDetail.setCurrentOrderStaus(1);*/
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

         /*switch (orderDetail.getSettlementType()) {
            //1货到付款
           case 1:
                //直接取消掉订单就可以了
                break;
            //0 线上支付
            case 0:

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
        }*/
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

    public OrderDetail order(OrderDetail orderDetail){
        return orderMapper.order(orderDetail);
    }

    public List<OrderDetail> lateOrders(OrderParam orderParam){
        return orderMapper.lateOrders(orderParam);
    }

    public OrderDetail lateOneOrder(long userId){
        return orderMapper.lateOneOrder(userId);
    }

    public Integer lateOrdersFee(OrderParam orderParam){
        return orderMapper.lateOrdersFee(orderParam);
    }
}
