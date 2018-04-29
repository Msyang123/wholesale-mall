package com.lhiot.mall.wholesale.goods.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.goods.domain.GoodsKeywords;
import com.lhiot.mall.wholesale.goods.domain.girdparam.KeywordsGirdParam;

@Mapper
public interface GoodsKeywordsMapper {

    int insert(GoodsKeywords goodsKeywords);

    int update(GoodsKeywords goodsKeywords);

    void removeInbatch(List<Long> id);

    GoodsKeywords select(long id);

    List<GoodsKeywords> search();
    
    List<GoodsKeywords> pageQuery(KeywordsGirdParam param);
    
    int pageQueryCount(KeywordsGirdParam param);
    
    List<GoodsKeywords> keywords(Map<String,Object> map);
}
