package com.lhiot.mall.wholesale.pay.api;

import com.leon.microx.common.exception.ServiceException;
import com.lhiot.mall.wholesale.order.domain.DebtOrder;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.service.DebtOrderService;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.pay.service.PayService;
import com.lhiot.mall.wholesale.user.wechat.PaymentProperties;
import com.lhiot.mall.wholesale.user.wechat.WeChatUtil;
import com.lhiot.mall.wholesale.user.wechat.XPathParser;
import com.lhiot.mall.wholesale.user.wechat.XPathWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Slf4j
@Api(description = "余额支付接口")
@RestController
@RequestMapping("/currency")
public class CurrencyPayApi {

	private final PayService payService;

    private final DebtOrderService debtOrderService;
    private final OrderService orderService;

	@Autowired
	public CurrencyPayApi(PayService payService,DebtOrderService debtOrderService, OrderService orderService){

        this.payService = payService;
        this.debtOrderService=debtOrderService;
        this.orderService=orderService;
	}
	
    @PutMapping("/orderpay/{orderCode}")
    @ApiOperation(value = "余额支付订单", response = String.class)
    public ResponseEntity orderPay(@PathVariable("orderCode") String orderCode) {
        OrderDetail orderDetail = orderService.searchOrder(orderCode);
        if (Objects.isNull(orderDetail)){
            return ResponseEntity.badRequest().body("没有该订单信息");
        }
        if(orderDetail.getOrderStatus()>2||orderDetail.getPayStatus()!=0){
            return ResponseEntity.badRequest().body("已支付订单状态，请勿重复支付");
        }
        int payResult=payService.currencyPay(orderDetail);
        if(payResult>0){
            return ResponseEntity.ok(orderDetail);
        }
        return ResponseEntity.badRequest().body("余额支付订单失败");
    }

    @PutMapping("/debtorderpay/{orderDebtCode}")
    @ApiOperation(value = "余额支付账款订单", response = String.class)
    public ResponseEntity debtorderPay(@PathVariable("orderDebtCode") String orderDebtCode){

        //依据欠款订单业务编码查询欠款订单信息
        DebtOrder debtOrder= debtOrderService.findByCode(orderDebtCode);
        //审核状态 0-未支付 1-审核中 2-审核失败 3-已支付
        if(Objects.isNull(debtOrder)){
            return ResponseEntity.badRequest().body("未找到欠款订单信息");
        }else if(debtOrder.getCheckStatus()==1){
            return ResponseEntity.badRequest().body("欠款订单审核中");
        }else if(debtOrder.getCheckStatus()==3){
            return ResponseEntity.badRequest().body("欠款订单已支付");
        }
        //余额支付账款订单支付
        int payResult=payService.currencyPay(debtOrder);
        if(payResult>0){
            return ResponseEntity.ok(debtOrder);
        }
        return ResponseEntity.badRequest().body("余额支付账款订单失败");
    }


}
