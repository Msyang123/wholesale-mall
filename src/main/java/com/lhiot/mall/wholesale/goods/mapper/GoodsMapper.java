package com.lhiot.mall.wholesale.goods.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.goods.domain.Goods;
import com.lhiot.mall.wholesale.goods.domain.girdparam.GoodsGirdParam;

@Mapper
public interface GoodsMapper {

    int insert(Goods user);

    int update(Goods user);

    void removeInbatch(List<Long> ids);

    Goods select(long id);

    List<Goods> search(Map<String, Object> where);
    
    //分页查询分类
    List<Goods> pageQuery(GoodsGirdParam param);
    //查询分类的总记录数
    int pageQueryCount(GoodsGirdParam param);
}
