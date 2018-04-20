package com.lhiot.mall.wholesale.goods.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.goods.domain.GoodsStandard;
import com.lhiot.mall.wholesale.goods.domain.girdparam.GoodsStandardGirdParam;

@Mapper
public interface GoodsStandardMapper {

    int insert(GoodsStandard GoodsStandard);

    int update(GoodsStandard GoodsStandard);

    void removeInbatch(List<Long> id);

    GoodsStandard select(long id);

    List<GoodsStandard> fuzzySearch(String name);
    
    //分页查询分类
    List<GoodsStandard> pageQuery(GoodsStandardGirdParam param);
    //查询分类的总记录数
    int pageQueryCount(GoodsStandardGirdParam param);
}
