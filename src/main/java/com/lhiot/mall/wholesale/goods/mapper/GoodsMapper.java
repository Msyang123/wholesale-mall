package com.lhiot.mall.wholesale.goods.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.goods.domain.Goods;
import com.lhiot.mall.wholesale.goods.domain.GoodsFlashsale;
import com.lhiot.mall.wholesale.goods.domain.GoodsInfo;
import com.lhiot.mall.wholesale.goods.domain.girdparam.GoodsGirdParam;

@Mapper
public interface GoodsMapper {

    int insert(Goods user);

    int update(Goods user);

    void removeInbatch(List<Long> ids);

    Goods select(long id);

    List<Goods> search(Map<String, Object> where);
    
    List<Goods> searchByCategory(List<Long> list);
    
    List<Goods> searchByCode(String goodsCode);
    //分页查询分类
    List<Goods> pageQuery(GoodsGirdParam param);
    //查询分类的总记录数
    int pageQueryCount(GoodsGirdParam param);

    GoodsInfo goodsInfo(long goods_id);

    GoodsFlashsale goodsFlashsale(long goodsStandardId);

    List<GoodsInfo> inventoryList(long userId);

    List<GoodsInfo> recommendList(long plateId);
    
    List<Goods> categoryGoods(Long categoryId);
    
    //关键词查询商品
    List<Goods> keywordGoods(String keyword);
    
    //根据版块id查询商品
    List<Goods> plateGoodses(Long plateId);

}
