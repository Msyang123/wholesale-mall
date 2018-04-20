package com.lhiot.mall.wholesale.goods.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.goods.domain.GoodsPriceRegion;

@Mapper
public interface GoodsPriceRegionMapper {

    int insert(GoodsPriceRegion goodsPriceRegion);

    int update(GoodsPriceRegion goodsPriceRegion);

    void removeInbatch(List<Long> ids);

    GoodsPriceRegion select(long id);
    
    //分页查询分类
    List<GoodsPriceRegion> pageQuery(Map<String,Object> map);
    //查询分类的总记录数
    int pageQueryCount(Map<String,Object> map);
}
