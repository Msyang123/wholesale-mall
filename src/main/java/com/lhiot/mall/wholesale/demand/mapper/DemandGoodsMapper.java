package com.lhiot.mall.wholesale.demand.mapper;

import com.lhiot.mall.wholesale.demand.domain.DemandGoods;
import com.lhiot.mall.wholesale.demand.domain.DemandGoodsResult;
import com.lhiot.mall.wholesale.demand.domain.gridparam.DemandGoodsGridParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;


@Mapper
public interface DemandGoodsMapper {

    DemandGoodsResult select(long id);

    //分页查询新品需求
    List<DemandGoodsResult> pageQuery(DemandGoodsGridParam param);

    //查询分类的总记录数
    int pageQueryCount(DemandGoodsGridParam param);

    int insertDemandGoods(DemandGoods demandGoods);
}