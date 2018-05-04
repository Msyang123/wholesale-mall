package com.lhiot.mall.wholesale.demand.api;

import com.leon.microx.common.wrapper.ResultObject;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.demand.domain.DemandGoods;
import com.lhiot.mall.wholesale.demand.domain.DemandGoodsResult;
import com.lhiot.mall.wholesale.demand.domain.gridparam.DemandGoodsGridParam;
import com.lhiot.mall.wholesale.demand.service.DemandGoodsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.Date;

@Api(description = "新品需求接口")
@Slf4j
@RestController
public class DemandGoodsApi {

    private final DemandGoodsService demandGoodsService;

    @Autowired
    public DemandGoodsApi(DemandGoodsService demandGoodsService) {
        this.demandGoodsService = demandGoodsService;
    }

    @PostMapping("/demandgoods/grid")
    @ApiOperation(value = "后台管理-分页查询新品需求", response = PageQueryObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) DemandGoodsGridParam param) throws IntrospectionException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return ResponseEntity.ok(demandGoodsService.pageQuery(param));
    }

    @GetMapping("/demandgoods/detail/{id}")
    @ApiOperation(value = "后台管理-新品需求详情页面",response = DemandGoodsResult.class)
    public  ResponseEntity<DemandGoodsResult> demandGoodsDetail(@PathVariable("id") Long id){
        return ResponseEntity.ok(demandGoodsService.detail(id));
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