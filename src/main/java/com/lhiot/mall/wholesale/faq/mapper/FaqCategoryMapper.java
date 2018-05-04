package com.lhiot.mall.wholesale.faq.mapper;

import com.lhiot.mall.wholesale.faq.domain.Faq;
import com.lhiot.mall.wholesale.faq.domain.FaqCategory;
import com.lhiot.mall.wholesale.faq.domain.gridparam.FaqGridParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FaqCategoryMapper {
    //查询FAQ分类的总数
    int pageQueryCount();

    int insertFaqCategory(FaqCategory faqCategory);

    List<FaqCategory> searchFaqCategory();
}
