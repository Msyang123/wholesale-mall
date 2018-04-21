package com.lhiot.mall.wholesale.demandgoods.api;

import com.leon.microx.common.wrapper.TipsObject;
import com.lhiot.mall.wholesale.demandgoods.service.DemandGoodsService;
import com.lhiot.mall.wholesale.demandgoods.vo.SearchDemandGoods;
import com.lhiot.mall.wholesale.demandgoods.vo.DemandGoods;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Created by HuFan on 2018/4/17.
 */
@Api
@Slf4j
@RestController
public class DemandGoodsApi {

    private final DemandGoodsService demandGoodsService;

    @Autowired
    public DemandGoodsApi(DemandGoodsService demandGoodsService) {
        this.demandGoodsService = demandGoodsService;
    }

    @PostMapping("/demandgoods")
    @ApiOperation(value = "添加新品需求", response = DemandGoods.class)
    public ResponseEntity add(@RequestBody DemandGoods demandGoods) {
        if (demandGoodsService.save(demandGoods)) {
            return ResponseEntity.created(URI.create("/demandgoods/" + demandGoods.getId())).body(demandGoods);
        }
        return ResponseEntity.badRequest().body(TipsObject.of("添加失败"));
    }

    @PutMapping("/demandgoods/{id}")
    @ApiOperation(value = "根据ID修改新品需求信息", response = DemandGoods.class)
    public ResponseEntity modify(@PathVariable("id") Long id, @RequestBody DemandGoods demandGoods) {
        demandGoods.setId(id);
        if (demandGoodsService.save(demandGoods)) {
            return ResponseEntity.ok(demandGoods);
        }
        return ResponseEntity.badRequest().body(TipsObject.of("修改失败"));
    }

    @DeleteMapping("/demandgoods/{id}")
    @ApiOperation(value = "根据ID删除一个新品需求")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        demandGoodsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/demandgoods/{id}")
    @ApiOperation(value = "根据ID查询一个新品需求信息", response = DemandGoods.class)
    public ResponseEntity<DemandGoods> demandGoods(@PathVariable("id") Long id) {
        return ResponseEntity.ok(demandGoodsService.demandGoods(id));
    }

    @PostMapping("/demandgoods/search")
    @ApiOperation(value = "新建一个查询，用于返回新品需求列表", response = DemandGoods.class, responseContainer = "List")
    public ResponseEntity<List<DemandGoods>> search(@RequestBody(required = false) SearchDemandGoods param) {
        return ResponseEntity.ok(demandGoodsService.demandGoods(param));
    }
}
