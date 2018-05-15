package com.lhiot.mall.wholesale.faq.service;

import com.lhiot.mall.wholesale.faq.domain.Faq;
import com.lhiot.mall.wholesale.faq.domain.FaqCategory;
import com.lhiot.mall.wholesale.faq.mapper.FaqCategoryMapper;
import com.lhiot.mall.wholesale.faq.mapper.FaqMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FaqCategoryService {

    private final FaqCategoryMapper faqCategoryMapper;

    @Autowired
    public FaqCategoryService(FaqCategoryMapper faqCategoryMapper) {
        this.faqCategoryMapper = faqCategoryMapper;
    }

    public int save(FaqCategory faqCategory) {
        return faqCategoryMapper.insertFaqCategory(faqCategory);
    }

    public List<FaqCategory> searchFaqCategory() {
        return faqCategoryMapper.searchFaqCategory();
    }

}
