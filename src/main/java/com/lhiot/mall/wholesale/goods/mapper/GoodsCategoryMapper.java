package com.lhiot.mall.wholesale.goods.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.goods.domain.GoodsCategory;
import com.lhiot.mall.wholesale.goods.domain.girdparam.GoodsCategoryGirdParam;

@Mapper
public interface GoodsCategoryMapper {

    int insert(GoodsCategory goodsCategory);

    int update(GoodsCategory goodsCategory);

    void removeInbatch(List<Long> ids);

    GoodsCategory select(long id);

    List<GoodsCategory> search();
    
    //分页查询分类
    List<GoodsCategory> pageQuery(GoodsCategoryGirdParam map);
    //查询分类的总记录数
    int pageQueryCount(GoodsCategoryGirdParam map);
}