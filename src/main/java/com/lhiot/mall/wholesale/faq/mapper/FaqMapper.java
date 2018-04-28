package com.lhiot.mall.wholesale.faq.mapper;

import com.lhiot.mall.wholesale.faq.domain.Faq;
import com.lhiot.mall.wholesale.faq.domain.FaqCategory;
import com.lhiot.mall.wholesale.faq.domain.gridparam.FaqGridParam;
import com.lhiot.mall.wholesale.introduction.domain.Introduction;
import com.lhiot.mall.wholesale.introduction.domain.gridparam.IntroductionGridParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FaqMapper {

    List<FaqCategory> searchFaqCategory();

    List<Faq> searchFaq(long proCategoryId);

    Faq select(long id);

    //分页查询新品需求
    List<Faq> pageQuery(FaqGridParam param);

    //查询分类的总记录数
    int pageQueryCount(FaqGridParam param);

    int update(Faq faq);

    int insert(Faq faq);
}
