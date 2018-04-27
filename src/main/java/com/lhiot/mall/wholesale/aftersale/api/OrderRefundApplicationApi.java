package com.lhiot.mall.wholesale.aftersale.api;

import com.leon.microx.common.wrapper.ArrayObject;
import com.lhiot.mall.wholesale.aftersale.domain.OrderRefundApplication;
import com.lhiot.mall.wholesale.aftersale.service.OrderRefundApplicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Api(description = "订单售后接口")
@Slf4j
@RestController
@RequestMapping("/aftersale")
public class OrderRefundApplicationApi {

    private final OrderRefundApplicationService orderRefundApplicationService;

    @Autowired
    public OrderRefundApplicationApi(OrderRefundApplicationService orderRefundApplicationService) {
        this.orderRefundApplicationService = orderRefundApplicationService;
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

    @PostMapping("/list")
    @ApiOperation(value = "售后申请列表")
    public ResponseEntity<ArrayObject> orderRefundApplicationList(@RequestBody OrderRefundApplication orderRefundApplication) {
        return ResponseEntity.ok(ArrayObject.of(orderRefundApplicationService.orderRefundApplicationList(orderRefundApplication)));
    }
}
