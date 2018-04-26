package com.lhiot.mall.wholesale.activity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.activity.domain.FlasesaleGoods;
import com.lhiot.mall.wholesale.activity.domain.FlashActivity;
import com.lhiot.mall.wholesale.activity.domain.gridparam.ActivityGirdParam;

@Mapper
public interface FlasesaleMapper {

    int insert(List<FlashActivity> list);

    int update(FlashActivity flashGoodsParam);

    void removeInbatch(List<Long> ids);
    
    FlashActivity select(Long id);
    
    List<FlashActivity> search(Long activityId);
    //分页查询分类
    List<FlasesaleGoods> pageQuery(ActivityGirdParam param);
    //查询分类的总记录数
    int pageQueryCount(ActivityGirdParam param);
}
