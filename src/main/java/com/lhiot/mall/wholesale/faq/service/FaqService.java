package com.lhiot.mall.wholesale.faq.service;

import com.lhiot.mall.wholesale.faq.domain.Faq;
import com.lhiot.mall.wholesale.faq.domain.FaqCategory;
import com.lhiot.mall.wholesale.faq.domain.gridparam.FaqGridParam;
import com.lhiot.mall.wholesale.faq.mapper.FaqMapper;
import com.lhiot.mall.wholesale.introduction.domain.Introduction;
import com.lhiot.mall.wholesale.introduction.domain.gridparam.IntroductionGridParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FaqService {

    private final FaqMapper faqMapper;

    @Autowired
    public FaqService(FaqMapper faqMapper) {
        this.faqMapper = faqMapper;
    }

    public List<FaqCategory> searchFaqCategory(){
        return faqMapper.searchFaqCategory();
    }

    public List<Faq> searchFaq(long faqCategoryId){
        return faqMapper.searchFaq(faqCategoryId);
    }

}
