package com.lhiot.mall.wholesale.pay.api;

import com.leon.microx.util.SnowflakeId;
import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.invoice.domain.Invoice;
import com.lhiot.mall.wholesale.invoice.service.InvoiceService;
import com.lhiot.mall.wholesale.order.domain.DebtOrder;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.service.DebtOrderService;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.pay.service.PayService;
import com.lhiot.mall.wholesale.pay.service.PaymentLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Api(description = "余额支付接口")
@RestController
@RequestMapping("/currency")
public class CurrencyPayApi {

	private final PayService payService;

    private final DebtOrderService debtOrderService;
    private final OrderService orderService;
    private final InvoiceService invoiceService;
    private final PaymentLogService paymentLogService;
    private final SnowflakeId snowflakeId;


	@Autowired
	public CurrencyPayApi(PayService payService, DebtOrderService debtOrderService, OrderService orderService,
                          InvoiceService invoiceService, PaymentLogService paymentLogService, SnowflakeId snowflakeId){

        this.payService = payService;
        this.debtOrderService=debtOrderService;
        this.orderService=orderService;
        this.invoiceService=invoiceService;
        this.paymentLogService=paymentLogService;
        this.snowflakeId = snowflakeId;
    }
	
    @PutMapping("/orderpay/{orderCode}")
    @ApiOperation(value = "余额支付订单", response = String.class)
    public ResponseEntity orderPay(@PathVariable("orderCode") String orderCode) throws Exception {
        OrderDetail orderDetail = orderService.searchOrder(orderCode);
        if (Objects.isNull(orderDetail)){
            return ResponseEntity.badRequest().body("没有该订单信息");
        }
        if(!Objects.equals(orderDetail.getOrderStatus(),"unpaid")){
            return ResponseEntity.badRequest().body("订单状态异常，请检查订单状态");
        }
        //余额支付订单 发送到总仓
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
        }else if(Objects.equals(debtOrder.getCheckStatus(),"unaudited")){
            return ResponseEntity.badRequest().body("欠款订单审核中");
        }else if(Objects.equals(debtOrder.getCheckStatus(),"paid") ||  Objects.equals(debtOrder.getCheckStatus(),"agree")){
            return ResponseEntity.badRequest().body("欠款订单已支付");
        }
        //余额支付账款订单支付
        int payResult=payService.currencyPay(debtOrder);
        if(payResult>0){
            return ResponseEntity.ok(debtOrder);
        }
        return ResponseEntity.badRequest().body("余额支付账款订单失败");
    }


    @PutMapping("/invoice-pay/{invoiceCode}")
    @ApiOperation(value = "余额支付发票", response = String.class)
    public ResponseEntity invoicePay(@RequestBody Invoice invoice) {
        //不允许重复开票
        String invoiceOrderCodes=invoice.getInvoiceOrderIds();
        if(StringUtils.isBlank(invoiceOrderCodes)){
            return ResponseEntity.badRequest().body("发票开票未包含订单信息");
        }
        for (String orderCode:invoiceOrderCodes.split(",")){
            if(Objects.nonNull(invoiceService.listByorderCodesLike(orderCode))){
                return ResponseEntity.badRequest().body("订单编码为("+orderCode+")的订单已申请或已开发票");
            }
        }
        //计算发票税费
        invoiceService.calculateTexFee(invoice);
        invoice.setInvoiceCode(snowflakeId.stringId());
        int payResult=payService.currencyPay(invoice);
        if(payResult>0){

            return ResponseEntity.ok(invoice);
        }
        return ResponseEntity.badRequest().body("余额支付发票失败");
    }

    @GetMapping("/orderpay/payment")
    @ApiOperation(value = "根据订单Ids查询支付记录",response = PaymentLog.class)
    public  ResponseEntity<List<PaymentLog>> paymentList(@PathVariable("orderIds") Long[] orderIds){
        List<Long> orderIdList =  Arrays.asList(orderIds);
        return ResponseEntity.ok(paymentLogService.getPaymentLogList(orderIdList));
    }
}
