package com.lhiot.mall.wholesale.demandgoods.service;

import com.leon.microx.util.BeanUtils;
import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.demandgoods.mapper.DemandGoodsMapper;
import com.lhiot.mall.wholesale.demandgoods.vo.SearchDemandGoods;
import com.lhiot.mall.wholesale.demandgoods.vo.DemandGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by HuFan on 2018/4/17.
 */
@Service
@Transactional
public class DemandGoodsService {

    private final SnowflakeId snowflakeId;

    private final DemandGoodsMapper demandGoodsMapper;

    @Autowired
    public DemandGoodsService(DemandGoodsMapper demandGoodsMapper, SnowflakeId snowflakeId) {
        this.demandGoodsMapper = demandGoodsMapper;
        this.snowflakeId = snowflakeId;
    }

    public boolean save(DemandGoods demandGoods) {
        if (demandGoods.getId() > 0) {
            return demandGoodsMapper.update(demandGoods) > 0;
        } else {
            demandGoods.setId(snowflakeId.longId());
            return demandGoodsMapper.insert(demandGoods) > 0;
        }
    }

   public void delete(long id) {
        demandGoodsMapper.remove(id);
    }

   public DemandGoods demandGoods(long id) {
        return demandGoodsMapper.select(id);
    }

   public List<DemandGoods> demandGoods(SearchDemandGoods param) {
        return demandGoodsMapper.search(BeanUtils.toMap(param));
   }


}
