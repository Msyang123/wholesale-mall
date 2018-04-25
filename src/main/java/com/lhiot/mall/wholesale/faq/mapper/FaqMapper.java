package com.lhiot.mall.wholesale.faq.mapper;

import com.lhiot.mall.wholesale.faq.domain.Faq;
import com.lhiot.mall.wholesale.faq.domain.FaqCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FaqMapper {

    List<FaqCategory> searchFaqCategory();

    List<Faq> searchFaq(long proCategoryId);
}
