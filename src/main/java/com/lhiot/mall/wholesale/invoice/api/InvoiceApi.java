package com.lhiot.mall.wholesale.invoice.api;

import com.leon.microx.common.wrapper.ArrayObject;
import com.lhiot.mall.wholesale.invoice.domain.InvoiceTitle;
import com.lhiot.mall.wholesale.invoice.service.InvoiceService;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Api
@Slf4j
@RestController
public class InvoiceApi {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceApi(InvoiceService invoiceService) {

        this.invoiceService = invoiceService;
    }

    @GetMapping("/invoiceTitle/{id}")
    @ApiOperation(value = "查询发票抬头信息")
    public ResponseEntity<InvoiceTitle> invoiceTile(@PathVariable @NotNull long id) {
        return ResponseEntity.ok(invoiceService.selectInvoiceTitle(id));
    }

    @PutMapping("/saveOrUpdateInvoiceTitle")
    @ApiOperation(value = "新增/修改发票信息")
    public ResponseEntity saveOrUpdateInvoiceTitle(@RequestBody InvoiceTitle invoiceTitle){
        if (invoiceService.saveOrUpdateInvoiceTitle(invoiceTitle)>0){
            return ResponseEntity.ok().body("新增/修改完成");
        }else{
            return ResponseEntity.badRequest().body("新增/修改失败");
        }
    }




}
