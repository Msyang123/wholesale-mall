package com.lhiot.mall.wholesale.goods.api;

import java.net.URI;

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
import com.lhiot.mall.wholesale.goods.domain.Goods;
import com.lhiot.mall.wholesale.goods.domain.girdparam.GoodsGirdParam;
import com.lhiot.mall.wholesale.goods.service.GoodsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(description = "商品信息")
@RestController
@RequestMapping
public class GoodsApi {
	
	private final GoodsService goodsService;
	
	@Autowired
	public GoodsApi(GoodsService goodsService){
		this.goodsService = goodsService;
	}
	
    @PostMapping("/goods")
    @ApiOperation(value = "添加商品单位", response = Boolean.class)
    public ResponseEntity<?> add(@RequestBody Goods goods){
    	if(goodsService.create(goods)){
    		return ResponseEntity.created(URI.create("/goods/"+goods.getId()))
    				.body(goods);
    	}
    	return ResponseEntity.badRequest().body(ResultObject.of("添加失败"));
    }
    
    @PutMapping("/goods/{id}")
    @ApiOperation(value = "根据ID修改商品", response = Goods.class)
    public ResponseEntity<?> modify(@PathVariable("id") Long id, Goods goods) {
        if (goodsService.update(goods)) {
            return ResponseEntity.ok(goods);
        }
        return ResponseEntity.badRequest().body(ResultObject.of("修改失败"));
    }

    @DeleteMapping("/goods/{id}")
    @ApiOperation(value = "根据id批量删除商品")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
    	goodsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/goods/{id}")
    @ApiOperation(value = "根据ID查询商品", response = Goods.class)
    public ResponseEntity<Goods> goods(@PathVariable("id") Long id) {
        return ResponseEntity.ok(goodsService.goods(id));
    }
    
    @PostMapping("/goods/gird")
    @ApiOperation(value = "新建一个查询，分页查询商品", response = ArrayObject.class)
    public ResponseEntity<ArrayObject<PageObject>> grid(@RequestBody(required = true) GoodsGirdParam param) {
        return ResponseEntity.ok(goodsService.pageQuery(param));
    }
}
