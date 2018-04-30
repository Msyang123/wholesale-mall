package com.lhiot.mall.wholesale.goods.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.goods.domain.PlateGoods;
import com.lhiot.mall.wholesale.goods.domain.PlateGoodsResult;
import com.lhiot.mall.wholesale.goods.domain.girdparam.GoodsStandardGirdParam;

@Mapper
public interface PlateGoodsMapper {

    int insertInbatch(List<PlateGoods> list);

    void removeInbatch(List<Long> ids);
    
    int update(PlateGoods plateGoods);
    
    int updateInBatch(List<PlateGoods> plateGoods);

    PlateGoodsResult select(long id);

    List<PlateGoodsResult> search();
    //获取当前分类的最大排序值
    Integer maxRank(Long plateId);
    //根据分类id查询
    List<PlateGoods> findByPlateId(Long plateId);
    
    //分页查询分类
    List<PlateGoodsResult> pageQuery(GoodsStandardGirdParam param);
    //查询分类的总记录数
    int pageQueryCount(GoodsStandardGirdParam param);
}
