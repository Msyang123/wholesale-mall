package com.lhiot.mall.wholesale;

import com.lhiot.mall.wholesale.activity.service.FlashsaleService;
import com.lhiot.mall.wholesale.base.JacksonUtils;
import com.lhiot.mall.wholesale.coupon.service.CouponEntityService;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Service
@Slf4j
public class MqConsumer{

    private final OrderService orderService;
    private final CouponEntityService couponEntityService;
    private final FlashsaleService flashsaleService;

    private final RabbitTemplate rabbit;

    @Autowired
    public MqConsumer(OrderService orderService,CouponEntityService couponEntityService,FlashsaleService flashsaleService,RabbitTemplate rabbit){
        this.orderService=orderService;
        this.couponEntityService=couponEntityService;
        this.flashsaleService= flashsaleService;
        this.rabbit=rabbit;
    }

    /**
     * 处理订单超过三十分钟业务
     * @param getMessage 接收到的消息
     */
    @RabbitHandler
    @RabbitListener(queues = "order-repeat-queue")
    public void orderOutTime(String getMessage) {
        log.info("orderOutTime =========== " + getMessage);
        try {
            OrderDetail orderDetail=JacksonUtils.fromJson(getMessage, OrderDetail.class);
            OrderDetail searchOrderDetail= orderService.searchOrder(orderDetail.getOrderCode());

            if(Objects.nonNull(searchOrderDetail)) {
                //还是未支付状态 直接改成已失效
                if (Objects.equals("unpaid", searchOrderDetail.getOrderStatus())){
                    OrderDetail updateOrderDetail=new OrderDetail();
                    updateOrderDetail.setOrderCode(searchOrderDetail.getOrderCode());
                    updateOrderDetail.setOrderStatus("failed");
                    updateOrderDetail.setCurrentOrderStatus("unpaid");
                    orderService.updateOrderStatus(updateOrderDetail);
                }else if (Objects.equals("paying", searchOrderDetail.getOrderStatus())){
                    //继续往延迟队列中发送
                    rabbit.convertAndSend("order-direct-exchange", "order-dlx-queue", JacksonUtils.toJson(orderDetail), message -> {
                        message.getMessageProperties().setExpiration(String.valueOf(30 * 60 * 1000));
                        return message;
                    });
                }
            }
        }  catch (IOException e) {
            log.error("消息处理错误" + e.getLocalizedMessage());
        }
    }

    /**
     * 订单创建广播
     * @param getMessage
     */
    @RabbitHandler
    @RabbitListener(queues = "order-create-publisher")
    public void orderCreatePublisher(String getMessage){
        log.info("订单创建了"+getMessage);
        try {
            OrderDetail orderDetail = JacksonUtils.fromJson(getMessage, OrderDetail.class);
        }  catch (IOException e) {
            log.error("消息处理错误" + e.getLocalizedMessage());
        }
        //couponEntityService.delete("11");
    }

    /**
     * 订单支付优惠券设置为失效
     * @param getMessage
     */
    @RabbitHandler
    @RabbitListener(queues = "coupon-publisher")
    public void couponPublisher(String getMessage){
        log.info("coupon-publisher"+getMessage);
        try {
            OrderDetail orderDetail = JacksonUtils.fromJson(getMessage, OrderDetail.class);
        }  catch (IOException e) {
            log.error("消息处理错误" + e.getLocalizedMessage());
        }
        couponEntityService.delete("11");
    }

    /**
     * 订单支付后限时抢购活动处理
     * @param getMessage
     */
    @RabbitHandler
    @RabbitListener(queues = "flasesale-publisher")
    public void flasesalePublisher(String getMessage){
        log.info("flasesale-publisher"+getMessage);
        try {
            OrderDetail orderDetail = JacksonUtils.fromJson(getMessage, OrderDetail.class);
        }  catch (IOException e) {
            log.error("消息处理错误" + e.getLocalizedMessage());
        }
        //couponEntityService.delete("11");
    }

}
