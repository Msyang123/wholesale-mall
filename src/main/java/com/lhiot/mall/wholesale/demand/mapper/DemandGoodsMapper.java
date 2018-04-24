package com.lhiot.mall.wholesale.demand.mapper;

import com.lhiot.mall.wholesale.demand.domain.DemandGoods;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DemandGoodsMapper {

    int insertDemandGoods(DemandGoods demandGoods);
}
