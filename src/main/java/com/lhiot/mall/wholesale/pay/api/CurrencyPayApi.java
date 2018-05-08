package com.lhiot.mall.wholesale.pay.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.microx.common.exception.ServiceException;
import com.lhiot.mall.wholesale.invoice.domain.Invoice;
import com.lhiot.mall.wholesale.invoice.service.InvoiceService;
import com.leon.microx.common.wrapper.ArrayObject;
import com.lhiot.mall.wholesale.faq.domain.Faq;
import com.lhiot.mall.wholesale.faq.domain.FaqCategory;
import com.leon.microx.common.wrapper.ArrayObject;
import com.lhiot.mall.wholesale.invoice.domain.Invoice;
import com.lhiot.mall.wholesale.invoice.service.InvoiceService;
import com.lhiot.mall.wholesale.order.domain.DebtOrder;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.service.DebtOrderService;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.pay.hdsend.Inventory;
import com.lhiot.mall.wholesale.pay.hdsend.Warehouse;
import com.lhiot.mall.wholesale.pay.service.PayService;
import com.lhiot.mall.wholesale.pay.service.PaymentLogService;
import com.lhiot.mall.wholesale.pay.service.PaymentLogService;
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
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.List;

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
    private final Warehouse warehouse;


	@Autowired
	public CurrencyPayApi(PayService payService,DebtOrderService debtOrderService, OrderService orderService,
                          InvoiceService invoiceService,PaymentLogService paymentLogService,Warehouse warehouse){

        this.payService = payService;
        this.debtOrderService=debtOrderService;
        this.orderService=orderService;
        this.invoiceService=invoiceService;
        this.paymentLogService=paymentLogService;
        this.warehouse=warehouse;
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
        int payResult=payService.currencyPay(orderDetail);
        if(payResult>0){
            //FIXME 发送订单到海鼎总仓
            Inventory inventory=new Inventory();
            inventory.setUuid(UUID.randomUUID().toString());
            inventory.setSenderCode("9646");
            inventory.setSenderWrh("07310101");
            inventory.setReceiverCode(null);
            inventory.setContactor("老曹");
            inventory.setPhoneNumber("18888888888");
            inventory.setDeliverAddress("五一大道98号");
            inventory.setRemark("快点送");
            inventory.setOcrDate(new Date());
            inventory.setFiller("填单人");
            inventory.setSeller("销售员");
            inventory.setSouceOrderCls("批发商城");
            inventory.setNegInvFlag("1");
            inventory.setMemberCode(null);
            inventory.setFreight(new BigDecimal(21.3));

            //清单
            List<Inventory.WholeSaleDtl> wholeSaleDtlList=new ArrayList<>();
            Inventory.WholeSaleDtl wholeSaleDtl1=inventory.new WholeSaleDtl();
            wholeSaleDtl1.setSkuId("010100100011");
            wholeSaleDtl1.setQty(new BigDecimal(3));
            wholeSaleDtl1.setPrice(new BigDecimal(100.1));
            wholeSaleDtl1.setTotal(null);
            wholeSaleDtl1.setFreight(null);
            wholeSaleDtl1.setPayAmount(new BigDecimal((99.1)));
            wholeSaleDtl1.setUnitPrice(null);
            wholeSaleDtl1.setPriceAmount(new BigDecimal(200.1));
            wholeSaleDtl1.setBuyAmount(new BigDecimal(99.2));
            wholeSaleDtl1.setBusinessDiscount(new BigDecimal(0.1));
            wholeSaleDtl1.setPlatformDiscount(new BigDecimal(0));
            wholeSaleDtl1.setQpc(new BigDecimal(5));
            wholeSaleDtl1.setQpcStr("1*5");

            wholeSaleDtlList.add(wholeSaleDtl1);
            inventory.setProducts(wholeSaleDtlList);

            List<Inventory.Pay> pays=new ArrayList<>();
            Inventory.Pay pay=inventory.new Pay();

            pay.setTotal(new BigDecimal(234.56));
            pay.setPayName("现金支付");
            pays.add(pay);
            inventory.setPays(pays);

            ObjectMapper om=new ObjectMapper();
            String h4Result=warehouse.h4Request("post",
                    "/wholesaleservice/wholesale/savenew2state/1700",om.writeValueAsString(inventory));
            log.info(h4Result);
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
            //修改账款订单为已支付
            DebtOrder saveDebtOrder=new DebtOrder();
            saveDebtOrder.setOrderDebtCode(orderDebtCode);
            saveDebtOrder.setCheckStatus("paid");
            saveDebtOrder.setPaymentType("balance");
            debtOrderService.updateDebtOrderByCode(saveDebtOrder);
            return ResponseEntity.ok(debtOrder);
        }
        return ResponseEntity.badRequest().body("余额支付账款订单失败");
    }


    @PutMapping("/invoice-pay/{invoiceCode}")
    @ApiOperation(value = "余额支付发票", response = String.class)
    public ResponseEntity invoicePay(@PathVariable("invoiceCode") Long invoiceCode) {
        //依据发票业务编码查询发票信息
        Invoice invoice= invoiceService.findInvoiceByCode(invoiceCode);
        if(Objects.isNull(invoice)){
            return ResponseEntity.badRequest().body("未找到开票信息");
        }else if(invoice.getInvoiceStatus()=="no"){
            return ResponseEntity.badRequest().body("发票已支付，请勿重复支付");
        }else if(invoice.getInvoiceStatus()=="yes"){
            return ResponseEntity.badRequest().body("已经开票，请勿重复支付");
        }

        int payResult=payService.currencyPay(invoice);
        if(payResult>0){
            //保存开票信息
            invoiceService.applyInvoice(invoice);
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
