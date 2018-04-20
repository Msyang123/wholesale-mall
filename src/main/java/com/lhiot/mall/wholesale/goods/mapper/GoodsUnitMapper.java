package com.lhiot.mall.wholesale.goods.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.goods.domain.GoodsUnit;

@Mapper
public interface GoodsUnitMapper {

    int insert(GoodsUnit goodsUnit);

    int update(GoodsUnit goodsUnit);

    void removeInbatch(long id);

    GoodsUnit select(long id);
    
    GoodsUnit findByCode(Long code);

    List<GoodsUnit> search(Map<String, Object> where);
    
    //分页查询分类
    List<GoodsUnit> pageQuery(Map<String,Object> map);
    //查询分类的总记录数
    int pageQueryCount(Map<String,Object> map);
}
