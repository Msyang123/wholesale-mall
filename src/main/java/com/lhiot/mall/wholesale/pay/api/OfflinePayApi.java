package com.lhiot.mall.wholesale.pay.api;

import com.lhiot.mall.wholesale.order.domain.DebtOrder;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.service.DebtOrderService;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.pay.hdsend.Inventory;
import com.lhiot.mall.wholesale.pay.hdsend.Warehouse;
import com.lhiot.mall.wholesale.pay.service.PayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Api(description = "账款订单线下支付接口")
@RestController
@RequestMapping("/offline")
public class OfflinePayApi {

    private final OrderService orderService;
    private final PayService payService;
    private final DebtOrderService debtOrderService;
    private final Warehouse warehouse;

	@Autowired
	public OfflinePayApi(OrderService orderService, PayService payService, DebtOrderService debtOrderService, Warehouse warehouse){
        this.orderService = orderService;
        this.payService = payService;

        this.debtOrderService=debtOrderService;
        this.warehouse = warehouse;
    }

    @PutMapping("/confirm/{orderCode}")
    @ApiOperation(value = "依据订单编码修改订单为线下支付方式并且发货")
    public ResponseEntity<OrderDetail> confirm(@PathVariable("orderCode") String orderCode, @RequestBody OrderDetail orderDetail){
        //线下支付
        if(!Objects.equals(orderDetail.getSettlementType(),"offline")) {
            orderDetail.setCode(-1001);
            orderDetail.setMsg("非货到付款订单");
            return ResponseEntity.ok(orderDetail);
        }
        orderDetail.setOrderCode(orderCode);
        int result=payService.sendToStock(orderDetail);
        if(result>0){
            orderDetail.setCode(1001);
            orderDetail.setMsg("支付成功");
        }else{
            orderDetail.setCode(-1001);
            orderDetail.setMsg("支付失败");
        }
        return ResponseEntity.ok(orderDetail);
    }

    @PostMapping("/debtorderpay")
    @ApiOperation(value = "线下支付账款订单提交审核", response = String.class)
    public ResponseEntity debtorderPay(@RequestBody DebtOrder debtOrder){

        //依据欠款订单业务编码查询欠款订单信息
        DebtOrder searchDebtOrder= debtOrderService.findByCode(debtOrder.getOrderDebtCode());
        //审核状态：unpaid-未支付 failed-已失效 paid-已支付 unaudited-未审核 agree-审核通过 reject-审核不通过
        if(Objects.isNull(searchDebtOrder)){
            return ResponseEntity.badRequest().body("未找到欠款订单信息");
        }else if(Objects.equals(searchDebtOrder.getCheckStatus(),"unaudited")){
            return ResponseEntity.badRequest().body("欠款订单审核中");
        }else if(Objects.equals(searchDebtOrder.getCheckStatus(),"agree")||Objects.equals(searchDebtOrder.getCheckStatus(),"paid")){
            return ResponseEntity.badRequest().body("欠款订单已支付");
        }
        //提交账款订单审核
        debtOrder.setCheckStatus("unaudited");//审核状态：unpaid-未支付 failed-已失效 paid-已支付 unaudited-未审核 agree-审核通过 reject-审核不通过
        int result=debtOrderService.updateDebtOrderByCode(debtOrder);
        if(result>0){
            //修改账款订单状态
            return ResponseEntity.ok(debtOrder);
        }
        return ResponseEntity.badRequest().body("线下支付账款订单提交审核失败");
    }


}
