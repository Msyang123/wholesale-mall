package com.lhiot.mall.wholesale.goods.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.goods.domain.PlateCategory;

@Mapper
public interface PlateCategoryMapper {

    int insert(PlateCategory plateCategory);

    int update(PlateCategory plateCategory);

    void removeInbatch(List<Long> ids);

    PlateCategory select(long id);

    List<PlateCategory> search();
    
    //分页查询分类
    List<PlateCategory> pageQuery(Map<String,Object> map);
    //查询分类的总记录数
    int pageQueryCount();
}
