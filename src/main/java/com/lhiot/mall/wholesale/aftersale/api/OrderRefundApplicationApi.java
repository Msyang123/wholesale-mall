package com.lhiot.mall.wholesale.aftersale.api;

import com.leon.microx.common.wrapper.ArrayObject;
import com.lhiot.mall.wholesale.aftersale.domain.OrderRefundApplication;
import com.lhiot.mall.wholesale.aftersale.service.OrderRefundApplicationService;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;
import com.lhiot.mall.wholesale.order.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(description = "订单售后接口")
@Slf4j
@RestController
@RequestMapping("/after-sale")
public class OrderRefundApplicationApi {

    private final OrderRefundApplicationService orderRefundApplicationService;

    private final OrderService orderService;

    @Autowired
    public OrderRefundApplicationApi(OrderRefundApplicationService orderRefundApplicationService, OrderService orderService) {
        this.orderRefundApplicationService = orderRefundApplicationService;
        this.orderService = orderService;
    }

    @PostMapping("/apply")
    @ApiOperation(value = "售后申请")
    public ResponseEntity apply(@RequestBody OrderRefundApplication orderRefundApplication) {
        return ResponseEntity.ok(orderRefundApplicationService.create(orderRefundApplication));
    }

    @PutMapping("/verify")
    @ApiOperation(value = "售后审核")
    public ResponseEntity verify(@RequestBody OrderRefundApplication orderRefundApplication) {
        return ResponseEntity.ok(orderRefundApplicationService.updateById(orderRefundApplication));
    }

    @GetMapping("/list/{userId}")
    @ApiOperation(value = "售后申请列表")
    public ResponseEntity<ArrayObject> orderRefundApplicationList(@PathVariable("userId") Long userId) {
        List<OrderRefundApplication> orderRefundApplicationList = orderRefundApplicationService.orderRefundApplicationList(userId);
        for (OrderRefundApplication orderRefund : orderRefundApplicationList) {
            OrderDetail orderDetail = orderService.searchOrder(orderRefund.getOrderId());
             List<OrderGoods> orderGoodsList =orderService.searchOrderGoods(orderDetail.getId());
            orderRefund.setOrderGoodsList(orderGoodsList);
        }
        return ResponseEntity.ok(ArrayObject.of(orderRefundApplicationList));
    }
}
