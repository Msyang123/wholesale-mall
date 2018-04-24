package com.lhiot.mall.wholesale.demand.service;

import com.lhiot.mall.wholesale.demand.domain.DemandGoods;
import com.lhiot.mall.wholesale.demand.mapper.DemandGoodsMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DemandGoodsService {

    private final DemandGoodsMapper demandGoodsMapper;

    public DemandGoodsService(DemandGoodsMapper demandGoodsMapper) {
        this.demandGoodsMapper = demandGoodsMapper;
    }

    public int insertDemandGoods(DemandGoods demandGoods){
        return demandGoodsMapper.insertDemandGoods(demandGoods);
    }
}
