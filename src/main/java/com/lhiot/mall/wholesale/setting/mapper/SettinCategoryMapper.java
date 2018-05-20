package com.lhiot.mall.wholesale.setting.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.setting.domain.ParamCategory;
import com.lhiot.mall.wholesale.setting.domain.gridparam.ParamCategoryGirdParam;

@Mapper
public interface SettinCategoryMapper {
    int insert(ParamCategory paramCategory);

    int update(ParamCategory paramCategory);

    void removeInbatch(List<Long> ids);

    ParamCategory select(long id);
    
    List<ParamCategory> findTree(String paramType);
    
    //分页查询分类
    List<ParamCategory> pageQuery(ParamCategoryGirdParam param);
    //查询分类的总记录数
    int pageQueryCount(ParamCategoryGirdParam param);
}
