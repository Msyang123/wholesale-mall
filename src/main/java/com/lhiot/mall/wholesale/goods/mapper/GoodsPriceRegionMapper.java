package com.lhiot.mall.wholesale.goods.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.goods.domain.GoodsPriceRegion;
import com.lhiot.mall.wholesale.goods.domain.girdparam.PriceRegionGirdParam;

@Mapper
public interface GoodsPriceRegionMapper {

    int insert(GoodsPriceRegion goodsPriceRegion);

    int update(GoodsPriceRegion goodsPriceRegion);

    void removeInbatch(List<Long> ids);

    GoodsPriceRegion select(long id);
    
    //分页查询分类
    List<GoodsPriceRegion> pageQuery(PriceRegionGirdParam param);
    //查询分类的总记录数
    int pageQueryCount(PriceRegionGirdParam param);
}
