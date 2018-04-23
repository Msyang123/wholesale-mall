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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.leon.microx.common.wrapper.ArrayObject;
import com.leon.microx.common.wrapper.ResultObject;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.goods.domain.CategoryTree;
import com.lhiot.mall.wholesale.goods.domain.GoodsCategory;
import com.lhiot.mall.wholesale.goods.domain.girdparam.GoodsCategoryGirdParam;
import com.lhiot.mall.wholesale.goods.service.GoodsCategoryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(description = "商品分类")
@RestController
@RequestMapping
public class GoodsCategoryApi {
	
	private final GoodsCategoryService goodsCategoryService;
	
	@Autowired
	public GoodsCategoryApi(GoodsCategoryService goodsCategoryService){
		this.goodsCategoryService = goodsCategoryService;
	}
	
    @PostMapping("/goodscategory")
    @ApiOperation(value = "添加商品分类", response = Boolean.class)
    public ResponseEntity<?> add(@RequestBody GoodsCategory goodsCategory){
    	if(goodsCategoryService.create(goodsCategory)){
    		return ResponseEntity.created(URI.create("/goodscategory/"+goodsCategory.getId()))
    				.body(goodsCategory);
    	}
    	return ResponseEntity.badRequest().body(ResultObject.of("添加失败"));
    }
    
    @PutMapping("/goodscategory/{id}")
    @ApiOperation(value = "根据ID修改商品分类", response = GoodsCategory.class)
    public ResponseEntity<?> modify(@PathVariable("id") Long id, @RequestBody GoodsCategory goodsCategory) {
        if (goodsCategoryService.update(goodsCategory)) {
            return ResponseEntity.ok(goodsCategory);
        }
        return ResponseEntity.badRequest().body(ResultObject.of("修改失败"));
    }

    @DeleteMapping("/goodscategory/{id}")
    @ApiOperation(value = "根据id批量删除商品分类")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
    	goodsCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/goodscategory/{id}")
    @ApiOperation(value = "根据ID查询商分类", response = GoodsCategory.class)
    public ResponseEntity<GoodsCategory> goodsCategory(@PathVariable("id") Long id) {
        return ResponseEntity.ok(goodsCategoryService.goodsCategory(id));
    }
    
    @PostMapping("/goodscategory/gird")
    @ApiOperation(value = "新建一个查询，分页查询商品分类", response = ArrayObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) GoodsCategoryGirdParam param) {
        return ResponseEntity.ok(goodsCategoryService.pageQuery(param));
    }
    
    @SuppressWarnings("unchecked")
	@GetMapping("/goodscategory/tree")
    @ApiOperation(value = "查询商品分类树结构", response = ArrayObject.class)
    public ResponseEntity<ArrayObject<CategoryTree>> tree() {
        return ResponseEntity.ok(ArrayObject.of(goodsCategoryService.tree()));
    }
    
	@GetMapping("/goodscategory/trydelete/{ids}")
    @ApiOperation(value = "查询商品分类是否可以被删除")
    public ResponseEntity<String> tryOperation(@PathVariable("ids") String ids) {
        return ResponseEntity.ok(goodsCategoryService.canDelete(ids));
    }
	
	@PostMapping("/goodscategory/tryoperation")
    @ApiOperation(value = "查询商品分类是否可以被修改或新增")
    public ResponseEntity<Boolean> tryoperation(@RequestBody(required = true) GoodsCategory goodsCategory) {
        return ResponseEntity.ok(goodsCategoryService.allowOperation(goodsCategory));
    }
}
