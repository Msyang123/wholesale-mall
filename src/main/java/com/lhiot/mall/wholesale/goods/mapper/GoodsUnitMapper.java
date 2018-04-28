package com.lhiot.mall.wholesale.goods.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.goods.domain.GoodsUnit;
import com.lhiot.mall.wholesale.goods.domain.girdparam.GoodsUnitGridParam;

@Mapper
public interface GoodsUnitMapper {

    int insert(GoodsUnit goodsUnit);

    int update(GoodsUnit goodsUnit);

    void removeInbatch(List<Long> id);

    GoodsUnit select(long id);
    
    List<GoodsUnit> findByCode(String code);

    List<GoodsUnit> search();
    
    int tryAdd(GoodsUnit goodsUnit);
    
    List<Long> searchFromGoods();
    
    List<GoodsUnit> searchInbatch(List<Long> ids);
    
    //分页查询分类
    List<GoodsUnit> pageQuery(GoodsUnitGridParam goodsUnitGridParam);
    //查询分类的总记录数
    int pageQueryCount(GoodsUnitGridParam goodsUnitGridParam);
}
