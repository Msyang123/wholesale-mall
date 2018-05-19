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
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        if(Objects.isNull(demandGoods.getGoodsName())||Objects.equals(demandGoods.getGoodsName(),"")
                ||Objects.isNull(demandGoods.getGoodsBrand())||Objects.equals(demandGoods.getGoodsBrand(),"")
                ||Objects.isNull(demandGoods.getGoodsStandard())||Objects.equals(demandGoods.getGoodsStandard(),"")){
            return ResponseEntity.badRequest().body("请完善信息");
        }
        demandGoods.setCreateTime(new Timestamp(new Date().getTime()));
        if (demandGoodsService.insertDemandGoods(demandGoods)>0){
            return ResponseEntity.ok().body("提交成功");
        }else{
            return ResponseEntity.badRequest().body("提交失败");
        }
    }

    @PostMapping("demandgoods/export")
    @ApiOperation(value = "后台管理系统新建一个查询，数据导出", response = DemandGoodsResult.class,responseContainer="list")
    public ResponseEntity<List<Map<String, Object>>> exportData(@RequestBody(required = true) DemandGoodsGridParam param) {
        return ResponseEntity.ok(demandGoodsService.exportData(param));
    }
}