package com.lhiot.mall.wholesale.goods.api;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leon.microx.common.wrapper.ArrayObject;
import com.leon.microx.common.wrapper.ResultObject;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.goods.domain.GoodsStandard;
import com.lhiot.mall.wholesale.goods.domain.PlateGoods;
import com.lhiot.mall.wholesale.goods.domain.girdparam.GoodsStandardGirdParam;
import com.lhiot.mall.wholesale.goods.service.PlateGoodsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(description = "版块商品")
@RestController
@RequestMapping
public class PlateGoodsApi {
	
	private final PlateGoodsService plateGoodsService;
	
	@Autowired
	public PlateGoodsApi(PlateGoodsService plateGoodsService){
		this.plateGoodsService = plateGoodsService;
	}
	
    @PostMapping("/plategoods")
    @ApiOperation(value = "添加商品版块商品", response = Boolean.class)
    public ResponseEntity<?> add(@RequestBody PlateGoods plategoods){
    	if(plateGoodsService.create(plategoods)){
    		return ResponseEntity.created(URI.create("/plategoods/"+plategoods.getId()))
    				.body(plategoods);
    	}
    	return ResponseEntity.badRequest().body(ResultObject.of("添加失败"));
    }

    @DeleteMapping("/plategoods/{id}")
    @ApiOperation(value = "根据id批量删除商品版块商品")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
    	plateGoodsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/plategoods/{id}")
    @ApiOperation(value = "根据ID查询商品版块商品", response = GoodsStandard.class)
    public ResponseEntity<GoodsStandard> plategoods(@PathVariable("id") Long id) {
        return ResponseEntity.ok(plateGoodsService.plateGoods(id));
    }

    @GetMapping("/plategoods/search")
    @ApiOperation(value = "新建一个查询，查询所有商品版块商品", response = GoodsStandard.class, responseContainer = "List")
    public ResponseEntity<List<GoodsStandard>> search() {
        return ResponseEntity.ok(plateGoodsService.search());
    }
    
    @PostMapping("/plategoods/gird")
    @ApiOperation(value = "新建一个查询，分页查询商品版块商品", response = ArrayObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) GoodsStandardGirdParam param) {
        return ResponseEntity.ok(plateGoodsService.pageQuery(param));
    }
    
    @PutMapping("/plategoods/{id}")
    @ApiOperation(value = "修改商品版块", response = Boolean.class)
    public ResponseEntity<?> modify(@PathVariable("id") Long id, PlateGoods plateGoods) {
        if (plateGoodsService.updatePlate(plateGoods)) {
            return ResponseEntity.ok(plateGoods);
        }
        return ResponseEntity.badRequest().body(ResultObject.of("修改失败"));
    }
}
