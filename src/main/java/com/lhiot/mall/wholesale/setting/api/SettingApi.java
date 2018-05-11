package com.lhiot.mall.wholesale.setting.api;

import com.leon.microx.common.wrapper.ArrayObject;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.invoice.domain.Invoice;
import com.lhiot.mall.wholesale.invoice.domain.InvoiceTitle;
import com.lhiot.mall.wholesale.invoice.domain.gridparam.InvoiceGridParam;
import com.lhiot.mall.wholesale.invoice.service.InvoiceService;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.setting.domain.ParamConfig;
import com.lhiot.mall.wholesale.setting.service.SettingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Api(description ="系统参数配置")
@Slf4j
@RestController
@RequestMapping("/config")
public class SettingApi {

    private final SettingService settingService;


    @Autowired
    public SettingApi( SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping("/search/{key}")
    @ApiOperation(value = "根据key查询参数值")
    public ResponseEntity<ParamConfig> invoiceTile(@PathVariable @NotNull String key) {
        return ResponseEntity.ok(settingService.searchConfigParam(key));
    }


}
