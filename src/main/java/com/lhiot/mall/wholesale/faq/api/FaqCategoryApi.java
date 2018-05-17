package com.lhiot.mall.wholesale.faq.api;

import com.lhiot.mall.wholesale.faq.domain.Faq;
import com.lhiot.mall.wholesale.faq.domain.FaqCategory;
import com.lhiot.mall.wholesale.faq.service.FaqCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Api(description = "FAQ分类帮助接口")
@Slf4j
@RestController
public class FaqCategoryApi {

    private final FaqCategoryService faqCategoryService;

    @Autowired
    public FaqCategoryApi(FaqCategoryService faqCategoryService) {
        this.faqCategoryService = faqCategoryService;
    }


    @PostMapping("/faqcategory/add")
    @ApiOperation(value = "后台管理系统---新增FAQ分类",response = FaqCategory.class)
    public  ResponseEntity add(@RequestBody FaqCategory faqCategory) {
        faqCategory.setCreateTime(new Timestamp(new Date().getTime()));
        if (faqCategoryService.save(faqCategory)>0) {
            return ResponseEntity.ok().body("新增完成");
        }
        return ResponseEntity.badRequest().body("添加失败");
    }

    @GetMapping("/faqcategory/searchfaqcategory")
    @ApiOperation(value = "后台管理系统----查询faq分类接口")
    public ResponseEntity<List<FaqCategory>> searchFaqCategory() {
        List<FaqCategory> faqCategoryList = faqCategoryService.searchFaqCategory();
        return ResponseEntity.ok(faqCategoryList);
    }
    
    @GetMapping("/faqcategory/tree")
    @ApiOperation(value = "后台管理系统----查询faq分类tree")
    public ResponseEntity<List<FaqCategory>> searchTree() {
        return ResponseEntity.ok(faqCategoryService.tree());
    }
}
