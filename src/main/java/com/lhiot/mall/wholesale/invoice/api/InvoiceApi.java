package com.lhiot.mall.wholesale.invoice.api;

import com.lhiot.mall.wholesale.invoice.domain.Invoice;
import com.lhiot.mall.wholesale.invoice.domain.InvoiceTitle;
import com.lhiot.mall.wholesale.invoice.service.InvoiceService;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.domain.UserAddress;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Api
@Slf4j
@RestController
public class InvoiceApi {

    private final InvoiceService invoiceService;

    private final OrderService orderService;

    @Autowired
    public InvoiceApi(InvoiceService invoiceService,OrderService orderService) {

        this.invoiceService = invoiceService;
        this.orderService=orderService;
    }

    @GetMapping("/invoiceTitle/{id}")
    @ApiOperation(value = "查询发票抬头信息")
    public ResponseEntity<InvoiceTitle> invoiceTile(@PathVariable @NotNull long id) {
        return ResponseEntity.ok(invoiceService.selectInvoiceTitle(id));
    }

    @PutMapping("/saveOrUpdateInvoiceTitle")
    @ApiOperation(value = "新增/修改个人信息")
    public ResponseEntity saveOrUpdateInvoiceTitle(@RequestBody InvoiceTitle invoiceTitle){
        if (invoiceService.saveOrUpdateInvoiceTitle(invoiceTitle)>0){
            return ResponseEntity.ok().body("新增/修改完成");
        }else{
            return ResponseEntity.badRequest().body("新增/修改失败");
        }
    }

    @GetMapping("/saveOrUpdateInvoiceTitle")
    @ApiOperation(value = "显示订单开票相关信息")
    public ResponseEntity orderNeedInvoice(@RequestParam("orderCodes") String orderCodes){
        if (StringUtils.isEmpty(orderCodes)){
            return ResponseEntity.badRequest().body("请现在开票订单");
        }
       List<OrderDetail> orderDetailList= orderService.searchOrdersByOrderCodes(orderCodes.split(","));
        //计算订单的开票金额
        int invoiceFee=0;
        for (OrderDetail item:orderDetailList) {
            invoiceFee+=item.getOrderNeedFee()+item.getDeliveryFee();
        }
        BigDecimal invoiceTax=new BigDecimal(0.0336f);
        int taxFee=(int)(invoiceFee*invoiceTax.floatValue());

        Invoice invoice=new Invoice();
        invoice.setInvoiceFee(invoiceFee);
        invoice.setTaxFee(taxFee);
        invoice.setInvoiceTax(invoiceTax);
        invoice.setInvoiceType(1);//发票类型
        return ResponseEntity.ok(invoice);
    }

    @PostMapping("/applyInvoice")
    @ApiOperation(value = "申请开票")
    public ResponseEntity applyInvoice(@RequestBody Invoice invoice){
        int result=invoiceService.applyInvoice(invoice);
        if(result>0){
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.badRequest().body("开票失败");
    }

    @GetMapping("/invoice/{invoiceCode}")
    @ApiOperation(value = "开票信息查询")
    public ResponseEntity applyInvoice(@PathVariable("invoiceCode") String invoiceCode){
        if(StringUtils.isEmpty(invoiceCode)){
            return ResponseEntity.badRequest().body("请传入发票编码");
        }
        return ResponseEntity.ok(invoiceService.findInvoiceByCode(invoiceCode));
    }




}
