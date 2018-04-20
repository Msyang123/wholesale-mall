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
import com.lhiot.mall.wholesale.goods.domain.GoodsPriceRegion;
import com.lhiot.mall.wholesale.goods.domain.girdparam.PriceRegionGirdParam;
import com.lhiot.mall.wholesale.goods.service.GoodsPriceRegionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(description = "商品价格区间")
@RestController
@RequestMapping
public class GoodsPriceRegionApi {
	
	private final GoodsPriceRegionService goodsPriceRegionService;
	
	@Autowired
	public GoodsPriceRegionApi(GoodsPriceRegionService goodsPriceRegionService){
		this.goodsPriceRegionService = goodsPriceRegionService;
	}
	
    @PostMapping("/priceregion")
    @ApiOperation(value = "添加价格区间", response = Boolean.class)
    public ResponseEntity<?> add(@RequestBody GoodsPriceRegion goodsPriceRegion){
    	if(goodsPriceRegionService.create(goodsPriceRegion)){
    		return ResponseEntity.created(URI.create("/priceregion/"+goodsPriceRegion.getId()))
    				.body(goodsPriceRegion);
    	}
    	return ResponseEntity.badRequest().body(ResultObject.of("添加失败"));
    }
    
    @PutMapping("/priceregion/{id}")
    @ApiOperation(value = "根据ID修改价格区间", response = GoodsPriceRegion.class)
    public ResponseEntity<?> modify(@PathVariable("id") Long id, GoodsPriceRegion goodsPriceRegion) {
        if (goodsPriceRegionService.update(goodsPriceRegion)) {
            return ResponseEntity.ok(goodsPriceRegion);
        }
        return ResponseEntity.badRequest().body(ResultObject.of("修改失败"));
    }

    @DeleteMapping("/priceregion/{id}")
    @ApiOperation(value = "根据id批量删除价格区间")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
    	goodsPriceRegionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/priceregion/{id}")
    @ApiOperation(value = "根据ID查询价格区间", response = GoodsPriceRegion.class)
    public ResponseEntity<GoodsPriceRegion> priceRegion(@PathVariable("id") Long id) {
        return ResponseEntity.ok(goodsPriceRegionService.GoodsPriceRegion(id));
    }
    
    @PostMapping("/priceregion/gird")
    @ApiOperation(value = "新建一个查询，分页查询价格区间", response = ArrayObject.class)
    public ResponseEntity<ArrayObject<PageObject>> grid(@RequestBody(required = true) PriceRegionGirdParam param) {
        return ResponseEntity.ok(goodsPriceRegionService.pageQuery(param));
    }
}
