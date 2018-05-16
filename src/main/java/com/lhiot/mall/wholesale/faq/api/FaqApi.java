package com.lhiot.mall.wholesale.faq.api;

import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.faq.domain.Faq;
import com.lhiot.mall.wholesale.faq.domain.FaqCategory;
import com.lhiot.mall.wholesale.faq.domain.gridparam.FaqGridParam;
import com.lhiot.mall.wholesale.faq.service.FaqCategoryService;
import com.lhiot.mall.wholesale.faq.service.FaqService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Api(description = "FAQ帮助接口")
@Slf4j
@RestController
public class FaqApi {

    private final FaqService faqService;
    private final FaqCategoryService faqCategoryService;

    @Autowired
    public FaqApi(FaqService faqService, FaqCategoryService faqCategoryService) {
        this.faqService = faqService;
        this.faqCategoryService = faqCategoryService;
    }

    @GetMapping("/faq")
    @ApiOperation(value = "查询faq分类接口")
    public ResponseEntity<List<FaqCategory>> faqSearch() {
        List<FaqCategory> faqCategoryList = faqCategoryService.searchFaqCategory();
        for (FaqCategory faqCategory:faqCategoryList) {
            List<Faq> faq = faqService.searchFaq(faqCategory.getId());
            faqCategory.setFaqList(faq);
        }
        return ResponseEntity.ok(faqCategoryList);
    }

    @GetMapping("/faq/detail/{id}")
    @ApiOperation(value = "查询faq详情",response = Faq.class)
    public  ResponseEntity<Faq> faqDetail(@PathVariable("id") Long id){
        return ResponseEntity.ok(faqService.detail(id));
    }

    @PutMapping("/faq/addorupdate")
    @ApiOperation(value = "后台管理系统---新增/修改FAQ",response = Faq.class)
    public  ResponseEntity saveFaq(@RequestBody Faq faq) {
        faq.setCreateTime(new Timestamp(new Date().getTime()));
        if (faqService.saveOrUpdateFaq(faq)>0){
            return ResponseEntity.ok().body("新增/修改完成");
        }else{
            return ResponseEntity.badRequest().body("新增/修改失败");
        }
    }

    @PostMapping("/faq/grid")
    @ApiOperation(value = "后台管理系统--分页查询FAQ", response = PageQueryObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) FaqGridParam param) {
        return ResponseEntity.ok(faqService.pageQuery(param));
    }

    @DeleteMapping("/faq/{id}")
    @ApiOperation(value = "根据id批量删除服务协议")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        faqService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
