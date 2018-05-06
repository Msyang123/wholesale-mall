package com.lhiot.mall.wholesale.invoice.api;

import com.leon.microx.common.wrapper.ArrayObject;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.invoice.domain.Invoice;
import com.lhiot.mall.wholesale.invoice.domain.InvoiceTitle;
import com.lhiot.mall.wholesale.invoice.domain.gridparam.InvoiceGridParam;
import com.lhiot.mall.wholesale.invoice.service.InvoiceService;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;
import com.lhiot.mall.wholesale.order.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Api(description ="开票接口")
@Slf4j
@RestController
@RequestMapping("/invoice")
public class InvoiceApi {

    private final InvoiceService invoiceService;

    private final OrderService orderService;

    @Autowired
    public InvoiceApi(InvoiceService invoiceService,OrderService orderService) {

        this.invoiceService = invoiceService;
        this.orderService=orderService;
    }

    @GetMapping("/search/{userId}")
    @ApiOperation(value = "查询发票抬头信息")
    public ResponseEntity<InvoiceTitle> invoiceTile(@PathVariable @NotNull long userId) {
        return ResponseEntity.ok(invoiceService.selectInvoiceTitle(userId));
    }

    @PostMapping("/create/title")
    @ApiOperation(value = "新增/修改发票信息")
    public ResponseEntity saveOrUpdateInvoiceTitle(@RequestBody InvoiceTitle invoiceTitle){
        if (invoiceService.saveOrUpdateInvoiceTitle(invoiceTitle)>0){
            return ResponseEntity.ok().body("新增/修改完成");
        }else{
            return ResponseEntity.badRequest().body("新增/修改失败");
        }
    }

    @GetMapping("/order-need-invoice")
    @ApiOperation(value = "显示订单开票相关信息")
    public ResponseEntity orderNeedInvoice(@RequestParam("orderCodes") String orderCodes){
        if (StringUtils.isEmpty(orderCodes)){
            return ResponseEntity.badRequest().body("请现在开票订单");
        }
       List<OrderDetail> orderDetailList= orderService.searchOrdersByOrderCodes(orderCodes.split(","));
        //计算订单的开票金额
        int invoiceFee=0;
        for (OrderDetail item:orderDetailList) {
            invoiceFee+=item.getPayableFee()+item.getDeliveryFee();
        }
        BigDecimal invoiceTax=new BigDecimal(0.0336f);
        int taxFee=(int)(invoiceFee*invoiceTax.floatValue());

        Invoice invoice=new Invoice();
        invoice.setInvoiceFee(invoiceFee);
        invoice.setTaxFee(taxFee);
        invoice.setInvoiceTax(invoiceTax);
        return ResponseEntity.ok(invoice);
    }

    @PostMapping("/apply-invoice")
    @ApiOperation(value = "申请开票")
    public ResponseEntity applyInvoice(@RequestBody Invoice invoice){
        int result=invoiceService.applyInvoice(invoice);
        if(result>0){
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.badRequest().body("开票失败");
    }

    @GetMapping("/{invoiceCode}")
    @ApiOperation(value = "开票信息查询")
    public ResponseEntity findInvoiceBycode(@PathVariable("invoiceCode") String invoiceCode){
        if(StringUtils.isEmpty(invoiceCode)){
            return ResponseEntity.badRequest().body("请传入发票编码");
        }
        return ResponseEntity.ok(invoiceService.findInvoiceByCode(invoiceCode));
    }

    @GetMapping("/record/{userId}")
    @ApiOperation(value = "开票信息记录查询")
    public ResponseEntity<ArrayObject> invoiceRecord(@PathVariable("userId") long userId){
        Invoice invoice = new Invoice();
        invoice.setUserId(userId);
        List<Invoice> invoiceList = invoiceService.list(invoice);
        OrderDetail orderDetail = new OrderDetail();
        for (Invoice item: invoiceList) {
            String orders = item.getInvoiceOrderIds();
            for (String orderCode:orders.split(",")) {
                orderDetail.setOrderCode(orderCode);
                OrderDetail order  = orderService.order(orderDetail);
                List<OrderGoods> orderGoodsList = orderService.searchOrderGoods(order.getId());
                order.setOrderGoodsList(orderGoodsList);
                item.setOrderDetail(order);
            }
        }
        return ResponseEntity.ok(ArrayObject.of(invoiceList));
    }

    @PostMapping("/grid")
    @ApiOperation(value = "后台管理-分页查询开票信息", response = PageQueryObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) InvoiceGridParam param) {
        return ResponseEntity.ok(invoiceService.pageQuery(param));
    }

    @GetMapping("/detail/{id}")
    @ApiOperation(value = "后台管理-开票信息详情页面",response = Invoice.class)
    public  ResponseEntity<Invoice> demandGoodsDetail(@PathVariable("id") Long id){
        return ResponseEntity.ok(invoiceService.detail(id));
    }

    @PutMapping("/status")
    @ApiOperation(value = "后台管理-修改开票状态")
    public ResponseEntity updateInvoiceStatus(@RequestBody Invoice invoice){
        if (invoiceService.updateInvoiceStatus(invoice)>0){
            return ResponseEntity.ok().body("修改完成");
        }else{
            return ResponseEntity.badRequest().body("修改失败");
        }
    }

    @PutMapping("/reject")
    @ApiOperation(value = "后台管理-修改驳回原因")
    public ResponseEntity updateInvoiceReason(@RequestBody Invoice invoice){
        if (invoiceService.updateInvoiceReason(invoice)>0){
            return ResponseEntity.ok().body("修改完成");
        }else{
            return ResponseEntity.badRequest().body("修改失败");
        }
    }

}
