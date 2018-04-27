package com.lhiot.mall.wholesale.pay.api;

import com.lhiot.mall.wholesale.order.domain.DebtOrder;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.service.DebtOrderService;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.pay.service.PayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@Api(description = "账款订单线下支付接口")
@RestController
@RequestMapping("/offline")
public class OfflinePayApi {

	private final PayService payService;
    private final DebtOrderService debtOrderService;

	@Autowired
	public OfflinePayApi(PayService payService, DebtOrderService debtOrderService){

        this.payService = payService;
        this.debtOrderService=debtOrderService;
	}


    @PostMapping("/debtorderpay")
    @ApiOperation(value = "线下支付账款订单提交审核", response = String.class)
    public ResponseEntity debtorderPay(@RequestBody DebtOrder debtOrder){

        //依据欠款订单业务编码查询欠款订单信息
        DebtOrder searchDebtOrder= debtOrderService.findByCode(debtOrder.getOrderDebtCode());
        //审核状态 0-未支付 1-审核中 2-审核失败 3-已支付
        if(Objects.isNull(searchDebtOrder)){
            return ResponseEntity.badRequest().body("未找到欠款订单信息");
        }else if(searchDebtOrder.getCheckStatus()==1){
            return ResponseEntity.badRequest().body("欠款订单审核中");
        }else if(searchDebtOrder.getCheckStatus()==3){
            return ResponseEntity.badRequest().body("欠款订单已支付");
        }
        //提交账款订单审核
        debtOrder.setCheckStatus(1);
        int result=debtOrderService.updateDebtOrderByCode(debtOrder);
        if(result>0){
            return ResponseEntity.ok(debtOrder);
        }
        return ResponseEntity.badRequest().body("线下支付账款订单提交审核失败");
    }


}
