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
import com.lhiot.mall.wholesale.goods.domain.GoodsKeywords;
import com.lhiot.mall.wholesale.goods.domain.KeywordsType;
import com.lhiot.mall.wholesale.goods.domain.girdparam.KeywordsGirdParam;
import com.lhiot.mall.wholesale.goods.service.GoodsKeywordService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(description = "商品关键词")
@RestController
@RequestMapping
public class GoodsKeywordsApi {
	
	private final GoodsKeywordService goodsKeywordService;
	
	@Autowired
	public GoodsKeywordsApi(GoodsKeywordService goodsKeywordService){
		this.goodsKeywordService = goodsKeywordService;
	}
	
    @PostMapping("/keywords")
    @ApiOperation(value = "添加商品关键词", response = Boolean.class)
    public ResponseEntity<?> add(@RequestBody GoodsKeywords goodsKeywords){
    	if(goodsKeywordService.create(goodsKeywords)){
    		return ResponseEntity.created(URI.create("/keywords/"+goodsKeywords.getId()))
    				.body(goodsKeywords);
    	}
    	return ResponseEntity.badRequest().body(ResultObject.of("添加失败"));
    }
    
    @PutMapping("/keywords/{id}")
    @ApiOperation(value = "根据ID修改商品关键词", response = GoodsKeywords.class)
    public ResponseEntity<?> modify(@PathVariable("id") Long id,@RequestBody GoodsKeywords goodsKeywords) {
        if (goodsKeywordService.update(goodsKeywords)) {
            return ResponseEntity.ok(goodsKeywords);
        }
        return ResponseEntity.badRequest().body(ResultObject.of("修改失败"));
    }

    @DeleteMapping("/keywords/{id}")
    @ApiOperation(value = "根据id批量删除商品关键词")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
    	goodsKeywordService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/keywords/{id}")
    @ApiOperation(value = "根据ID查询商品关键词", response = GoodsKeywords.class)
    public ResponseEntity<GoodsKeywords> keywords(@PathVariable("id") Long id) {
        return ResponseEntity.ok(goodsKeywordService.GoodsKeywords(id));
    }
    
    @PostMapping("/keywords/gird")
    @ApiOperation(value = "新建一个查询，分页查询商品关键词", response = ArrayObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) KeywordsGirdParam param) {
        return ResponseEntity.ok(goodsKeywordService.pageQuery(param));
    }
    
    @GetMapping("/keywords/keyword/{key}")
    @ApiOperation(value = "根据关键词，查询商品的关键词列表", response = GoodsKeywords.class,responseContainer="list")
    public ResponseEntity<List<GoodsKeywords>> keywords(@PathVariable("key") String keyword) {
        return ResponseEntity.ok(goodsKeywordService.keywords(keyword, KeywordsType.goods, null));
    }
}
