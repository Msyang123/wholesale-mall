package com.lhiot.mall.wholesale.faq.service;

import com.lhiot.mall.wholesale.faq.domain.FaqCategory;
import com.lhiot.mall.wholesale.faq.mapper.FaqCategoryMapper;
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

    //后台管理系统----FAQ分类下拉框
    public List<FaqCategory> searchFaqCategory() {
        return faqCategoryMapper.searchFaqCategory();
    }

    /**
     * 树结构
     * @return
     */
    public List<FaqCategory> tree(){
    	return faqCategoryMapper.ztree();
    }
}
