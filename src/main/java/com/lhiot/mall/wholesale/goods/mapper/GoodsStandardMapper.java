package com.lhiot.mall.wholesale.goods.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.goods.domain.GoodsStandard;

@Mapper
public interface GoodsStandardMapper {

    int insert(GoodsStandard GoodsStandard);

    int update(GoodsStandard GoodsStandard);

    void removeInbatch(long id);

    GoodsStandard select(long id);

    List<GoodsStandard> fuzzySearch(String name);
    
    //分页查询分类
    List<GoodsStandard> pageQuery(Map<String,Object> map);
    //查询分类的总记录数
    int pageQueryCount(Map<String,Object> map);
}
