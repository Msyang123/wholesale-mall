package com.lhiot.mall.wholesale.faq.api;

import com.lhiot.mall.wholesale.faq.domain.Faq;
import com.lhiot.mall.wholesale.faq.domain.FaqCategory;
import com.lhiot.mall.wholesale.faq.service.FaqService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

@Api(description = "faq帮助接口")
@Slf4j
@RestController
public class FaqApi {

    private final FaqService faqService;

    @Autowired
    public FaqApi(FaqService faqService) {
        this.faqService = faqService;
    }

    @GetMapping("/faq")
    @ApiOperation(value = "查询faq分类接口")
    public ResponseEntity<List<FaqCategory>> faqSearch() {
        List<FaqCategory> faqCategoryList = faqService.searchFaqCategory();
        for (FaqCategory faqCategory:faqCategoryList) {
            List<Faq> faq = faqService.searchFaq(faqCategory.getId());
            faqCategory.setFaqList(faq);
        }
        return ResponseEntity.ok(faqCategoryList);
    }
}
