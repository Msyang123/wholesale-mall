package com.lhiot.mall.wholesale.demand.api;

import com.leon.microx.common.wrapper.ResultObject;

import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.demand.domain.DemandGoods;
import com.lhiot.mall.wholesale.demand.domain.DemandGoodsResult;
import com.lhiot.mall.wholesale.demand.domain.gridparam.DemandGoodsGridParam;
import com.lhiot.mall.wholesale.demand.service.DemandGoodsService;
import com.lhiot.mall.wholesale.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


/**
 * Created by HuFan on 2018/4/23.
 */
@Api
@Slf4j
@RestController
public class DemandGoodsApi {

    private final DemandGoodsService demandGoodsService;

    private final UserService userService;

    @Autowired
    public DemandGoodsApi(DemandGoodsService demandGoodsService,UserService userService) {
        this.demandGoodsService = demandGoodsService;
        this.userService = userService;
    }

    @PostMapping("/demandgoods")
    @ApiOperation(value = "添加新品需求", response = DemandGoods.class)
    public ResponseEntity add(@RequestBody DemandGoods demandGoods) {
        if (demandGoodsService.save(demandGoods)) {
            return ResponseEntity.created(URI.create("/demandgoods/" + demandGoods.getId())).body(demandGoods);
        }
        return ResponseEntity.badRequest().body(ResultObject.of("添加失败"));
    }

    @PutMapping("/demandgoods/{id}")
    @ApiOperation(value = "根据ID修改新品需求信息", response = DemandGoods.class)
    public ResponseEntity modify(@PathVariable("id") Long id, @RequestBody DemandGoods demandGoods) {
        demandGoods.setId(id);
        if (demandGoodsService.save(demandGoods)) {
            return ResponseEntity.ok(demandGoods);
        }
        return ResponseEntity.badRequest().body(ResultObject.of("修改失败"));
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
    public ResponseEntity<List<DemandGoods>> search(@RequestBody(required = false) DemandGoodsGridParam param) {
        return ResponseEntity.ok(demandGoodsService.demandGoods(param));
    }

    @PostMapping("/demandgoods/grid")
    @ApiOperation(value = "新建一个查询，分页查询新品需求", response = PageQueryObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) DemandGoodsGridParam param) {
        return ResponseEntity.ok(demandGoodsService.pageQuery(param));
    }

    @GetMapping("/demandgoods/detail/{id}")
    @ApiOperation(value = "新品需求详情页面",response = DemandGoodsResult.class)
    public  ResponseEntity<DemandGoodsResult> demandGoodsDetail(@PathVariable("id") Long id){
        return ResponseEntity.ok(demandGoodsService.detail(id));
    }
}

