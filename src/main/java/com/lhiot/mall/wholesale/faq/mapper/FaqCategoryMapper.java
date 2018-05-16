package com.lhiot.mall.wholesale.faq.mapper;

import com.lhiot.mall.wholesale.faq.domain.FaqCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FaqCategoryMapper {
    //查询FAQ分类的总数
    int pageQueryCount();

    int insertFaqCategory(FaqCategory faqCategory);

    //后台管理系统----FAQ分类下拉框
    List<FaqCategory> searchFaqCategory();
}
