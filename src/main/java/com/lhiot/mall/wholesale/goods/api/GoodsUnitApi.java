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
import com.leon.microx.common.wrapper.PageObject;
import com.leon.microx.common.wrapper.ResultObject;
import com.lhiot.mall.wholesale.goods.domain.GoodsUnit;
import com.lhiot.mall.wholesale.goods.domain.girdparam.GoodsUnitGridParam;
import com.lhiot.mall.wholesale.goods.service.GoodsUnitService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(description = "商品单位")
@RestController
@RequestMapping
public class GoodsUnitApi {
	
	private final GoodsUnitService goodsUnitService;
	
	@Autowired
	public GoodsUnitApi(GoodsUnitService goodsUnitService){
		this.goodsUnitService = goodsUnitService;
	}
	
    @PostMapping("/goodsunit")
    @ApiOperation(value = "添加商品单位", response = Boolean.class)
    public ResponseEntity<?> add(@RequestBody GoodsUnit goodsUnit){
    	if(goodsUnitService.create(goodsUnit)){
    		return ResponseEntity.created(URI.create("/goodsunit/"+goodsUnit.getId()))
    				.body(goodsUnit);
    	}
    	return ResponseEntity.badRequest().body(ResultObject.of("添加失败"));
    }
    
    @PutMapping("/goodsunit/{id}")
    @ApiOperation(value = "根据ID修改商品单位", response = GoodsUnit.class)
    public ResponseEntity<?> modify(@PathVariable("id") Long id, GoodsUnit goodsUnit) {
        if (goodsUnitService.update(goodsUnit)) {
            return ResponseEntity.ok(goodsUnit);
        }
        return ResponseEntity.badRequest().body(ResultObject.of("修改失败"));
    }

    @DeleteMapping("/goodsunit/{id}")
    @ApiOperation(value = "根据id批量删除商品单位")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
    	goodsUnitService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/goodsunit/{id}")
    @ApiOperation(value = "根据ID查询商品单位", response = GoodsUnit.class)
    public ResponseEntity<GoodsUnit> goodsUnit(@PathVariable("id") Long id) {
        return ResponseEntity.ok(goodsUnitService.goodsUnit(id));
    }

    @GetMapping("/goodsunit/search")
    @ApiOperation(value = "新建一个查询，查询所有商品单位", response = GoodsUnit.class, responseContainer = "List")
    public ResponseEntity<List<GoodsUnit>> search() {
        return ResponseEntity.ok(goodsUnitService.search());
    }
    
    @PostMapping("/goodsunit/gird")
    @ApiOperation(value = "新建一个查询，分页查询商品单位", response = ArrayObject.class)
    public ResponseEntity<ArrayObject<PageObject>> grid(@RequestBody(required = true) GoodsUnitGridParam param) {
        return ResponseEntity.ok(goodsUnitService.pageQuery(param));
    }
}
