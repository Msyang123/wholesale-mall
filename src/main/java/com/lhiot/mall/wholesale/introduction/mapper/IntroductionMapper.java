package com.lhiot.mall.wholesale.introduction.mapper;

import com.lhiot.mall.wholesale.introduction.domain.Introduction;
import com.lhiot.mall.wholesale.introduction.domain.gridparam.IntroductionGridParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IntroductionMapper {

    Introduction select(long id);

    //分页查询新品需求
    List<Introduction> pageQuery(IntroductionGridParam param);

    //查询分类的总记录数
    int pageQueryCount(IntroductionGridParam param);

    int updateIntroduction(Introduction introduction);

    int insertIntroduction(Introduction introduction);
}