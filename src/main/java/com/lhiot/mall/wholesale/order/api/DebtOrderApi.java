package com.lhiot.mall.wholesale.order.api;

import com.leon.microx.common.exception.ServiceException;
import com.leon.microx.common.wrapper.ArrayObject;
import com.lhiot.mall.wholesale.order.domain.DebtOrder;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;
import com.lhiot.mall.wholesale.order.service.DebtOrderService;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import com.lhiot.mall.wholesale.user.service.SalesUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Api(description ="账款订单接口")
@Slf4j
@RestController
@RequestMapping("/debtorder")
public class DebtOrderApi {

    private final DebtOrderService debtOrderService;

    @Autowired
    public DebtOrderApi(DebtOrderService debtOrderService) {
        this.debtOrderService = debtOrderService;
    }



    @GetMapping("/{debtorderCode}")
    @ApiOperation(value = "根据订单编号查询订单详情")
    public ResponseEntity queryOrderByCode(@PathVariable("debtorderCode") String debtorderCode){
        DebtOrder debtOrder = debtOrderService.findByCode(debtorderCode);
        if (Objects.isNull(debtOrder)){
            return ResponseEntity.badRequest().body("未找到欠款订单信息");
        }
        return ResponseEntity.ok(debtOrder);
    }



}
