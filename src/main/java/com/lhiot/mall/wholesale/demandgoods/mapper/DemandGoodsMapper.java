package com.lhiot.mall.wholesale.demandgoods.mapper;

import com.lhiot.mall.wholesale.demandgoods.vo.DemandGoods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by HuFan on 2018/4/17.
 */
@Mapper
public interface DemandGoodsMapper {

    int insert(DemandGoods demandGoods);

    int update(DemandGoods demandGoods);

    void remove(long id);

    DemandGoods select(long id);

    List<DemandGoods> search(Map<String, Object> where);

}
