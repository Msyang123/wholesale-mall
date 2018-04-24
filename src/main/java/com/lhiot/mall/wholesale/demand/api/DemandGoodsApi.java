package com.lhiot.mall.wholesale.demand.api;

import com.leon.microx.common.wrapper.ResultObject;
import com.lhiot.mall.wholesale.demand.domain.DemandGoods;
import com.lhiot.mall.wholesale.demand.service.DemandGoodsService;
import com.lhiot.mall.wholesale.faq.service.FaqService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Api
@Slf4j
@RestController
public class DemandGoodsApi {

    private final DemandGoodsService demandGoodsService;

    @Autowired
    public DemandGoodsApi(DemandGoodsService demandGoodsService) {
        this.demandGoodsService = demandGoodsService;
    }

    @PostMapping("/demandGoods")
    @ApiOperation(value = "新品需求提交")
    public ResponseEntity demandGoods(@RequestBody DemandGoods demandGoods) {
        demandGoods.setCreateTime(new Timestamp(new Date().getTime()));
        if (demandGoodsService.insertDemandGoods(demandGoods)>0){
            return ResponseEntity.ok(ResultObject.of("提交成功"));
        }else{
            return ResponseEntity.ok(ResultObject.of("提交失败"));
        }
    }
}
