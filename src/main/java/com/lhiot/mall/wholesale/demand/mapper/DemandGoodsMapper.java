package com.lhiot.mall.wholesale.demand.mapper;


import com.lhiot.mall.wholesale.demand.domain.DemandGoods;
import com.lhiot.mall.wholesale.demand.domain.DemandGoodsResult;
import com.lhiot.mall.wholesale.demand.domain.gridparam.DemandGoodsGridParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;


@Mapper
public interface DemandGoodsMapper {

    int insert(DemandGoods demandGoods);

    int update(DemandGoods demandGoods);

    void remove(long id);

    DemandGoods select(long id);

    List<DemandGoods> search(Map<String, Object> where);

    //分页查询分类
    List<DemandGoodsResult> pageQuery(DemandGoodsGridParam param);

    //查询分类的总记录数
    int pageQueryCount(DemandGoodsGridParam param);
}
