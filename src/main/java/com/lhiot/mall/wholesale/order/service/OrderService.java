package com.lhiot.mall.wholesale.order.service;

import com.leon.microx.common.exception.ServiceException;
import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;
import com.lhiot.mall.wholesale.order.mapper.OrderMapper;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.pay.service.PayService;
import com.lhiot.mall.wholesale.pay.service.PaymentLogService;
import com.lhiot.mall.wholesale.user.wechat.PaymentProperties;
import com.lhiot.mall.wholesale.user.wechat.WeChatUtil;
import com.sgsl.hd.client.HaiDingClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@Transactional
public class OrderService {
    private final OrderMapper orderMapper;

    private final HaiDingClient hdClient;

    private final WeChatUtil weChatUtil;

    private final PaymentLogService paymentLogService;

    private final SnowflakeId snowflakeId;

    @Autowired
    public OrderService(OrderMapper orderMapper, HaiDingClient hdClient, PaymentLogService paymentLogService, PaymentProperties paymentProperties,SnowflakeId snowflakeId) {
        this.orderMapper = orderMapper;
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

    public Integer searchOutstandingAccountsOrder(String orderCode){
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
        orderDetail.setOrderStatus(0);
        orderDetail.setCurrentOrderStaus(1);
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

        switch (orderDetail.getOrderType()) {
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
        }
        return 1;
    }
}
