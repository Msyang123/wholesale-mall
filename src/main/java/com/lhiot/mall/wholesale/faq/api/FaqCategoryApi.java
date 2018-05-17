package com.lhiot.mall.wholesale.faq.api;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.faq.domain.FaqCategory;
import com.lhiot.mall.wholesale.faq.domain.FaqCategoryTree;
import com.lhiot.mall.wholesale.faq.domain.gridparam.FaqGridParam;
import com.lhiot.mall.wholesale.faq.service.FaqCategoryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(description = "FAQ分类帮助接口")
@Slf4j
@RestController
public class FaqCategoryApi {

    private final FaqCategoryService faqCategoryService;

    @Autowired
    public FaqCategoryApi(FaqCategoryService faqCategoryService) {
        this.faqCategoryService = faqCategoryService;
    }


    @PostMapping("/faqcategory/create")
    @ApiOperation(value = "后台管理系统---新增FAQ分类",response = String.class)
    public  ResponseEntity<?> add(@RequestBody FaqCategory faqCategory) {
        faqCategory.setCreateTime(new Timestamp(new Date().getTime()));
        if (faqCategoryService.save(faqCategory)>0) {
            return ResponseEntity.ok().body("新增完成");
        }
        return ResponseEntity.badRequest().body("添加失败");
    }
    
    @PutMapping("/faqcategory/update")
    @ApiOperation(value = "后台管理系统---修改FAQ分类",response = String.class)
    public  ResponseEntity<?> update(@RequestBody FaqCategory faqCategory) {
        faqCategory.setCreateTime(new Timestamp(new Date().getTime()));
        if (faqCategoryService.update(faqCategory)>0) {
            return ResponseEntity.ok().body("修改成功");
        }
        return ResponseEntity.badRequest().body("修改失败");
    }

    @GetMapping("/faqcategory/searchfaqcategory")
    @ApiOperation(value = "后台管理系统----查询faq分类接口")
    public ResponseEntity<List<FaqCategory>> searchFaqCategory() {
        List<FaqCategory> faqCategoryList = faqCategoryService.searchFaqCategory();
        return ResponseEntity.ok(faqCategoryList);
    }
    
    @GetMapping("/faqcategory/tree")
    @ApiOperation(value = "后台管理系统----查询faq分类tree")
    public ResponseEntity<List<FaqCategoryTree>> searchTree() {
        return ResponseEntity.ok(faqCategoryService.tree());
    }
    
    @DeleteMapping("/faqcategory/{id}")
    @ApiOperation(value = "根据id批量删除")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
    	faqCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/faqcategory/grid")
    @ApiOperation(value = "后台管理系统--分页查询FAQ分页", response = PageQueryObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) FaqGridParam param) {
        return ResponseEntity.ok(faqCategoryService.pageQuery(param));
    }
}
