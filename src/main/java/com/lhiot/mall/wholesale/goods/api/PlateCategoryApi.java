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
import com.lhiot.mall.wholesale.goods.domain.CategoryTree;
import com.lhiot.mall.wholesale.goods.domain.PlateCategory;
import com.lhiot.mall.wholesale.goods.domain.girdparam.PlateGirdParam;
import com.lhiot.mall.wholesale.goods.service.PlateCategoryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(description = "商品版块")
@RestController
@RequestMapping
public class PlateCategoryApi {
	
	private final PlateCategoryService plateCategoryService;
	
	@Autowired
	public PlateCategoryApi(PlateCategoryService plateCategoryService){
		this.plateCategoryService = plateCategoryService;
	}
	
    @PostMapping("/platecategory")
    @ApiOperation(value = "添加商品版块", response = Boolean.class)
    public ResponseEntity<?> add(@RequestBody PlateCategory platecategory){
    	if(plateCategoryService.create(platecategory)){
    		return ResponseEntity.created(URI.create("/platecategory/"+platecategory.getId()))
    				.body(platecategory);
    	}
    	return ResponseEntity.badRequest().body(ResultObject.of("添加失败"));
    }
    
    @PutMapping("/platecategory/{id}")
    @ApiOperation(value = "根据ID修改商品版块", response = PlateCategory.class)
    public ResponseEntity<?> modify(@PathVariable("id") Long id, @RequestBody PlateCategory platecategory) {
        if (plateCategoryService.update(platecategory)) {
            return ResponseEntity.ok(platecategory);
        }
        return ResponseEntity.badRequest().body(ResultObject.of("修改失败"));
    }

    @DeleteMapping("/platecategory/{id}")
    @ApiOperation(value = "根据id批量删除商品版块")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
    	plateCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/platecategory/{id}")
    @ApiOperation(value = "根据ID查询商品版块", response = PlateCategory.class)
    public ResponseEntity<PlateCategory> platecategory(@PathVariable("id") Long id) {
        return ResponseEntity.ok(plateCategoryService.plateCategory(id));
    }

    @GetMapping("/platecategory/search")
    @ApiOperation(value = "新建一个查询，查询所有商品版块", response = PlateCategory.class, responseContainer = "List")
    public ResponseEntity<List<PlateCategory>> search() {
        return ResponseEntity.ok(plateCategoryService.search());
    }
    
    @PostMapping("/platecategory/gird")
    @ApiOperation(value = "新建一个查询，分页查询商品版块", response = ArrayObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) PlateGirdParam param) {
        return ResponseEntity.ok(plateCategoryService.pageQuery(param));
    }
    
    @SuppressWarnings("unchecked")
	@GetMapping("/platecategory/tree")
    @ApiOperation(value = "查询商品版块的树结构", response = ArrayObject.class)
    public ResponseEntity<ArrayObject<CategoryTree>> grid() {
        return ResponseEntity.ok(ArrayObject.of(plateCategoryService.tree()));
    }
    
	@GetMapping("/platecategory/trydelete/{ids}")
    @ApiOperation(value = "查询商品版块是否可以被删除")
    public ResponseEntity<String> tryOperation(@PathVariable("ids") String ids) {
        return ResponseEntity.ok(plateCategoryService.canDelete(ids));
    }
}
