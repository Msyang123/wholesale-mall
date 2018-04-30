package com.lhiot.mall.wholesale.activity.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.activity.domain.FlashActivity;
import com.lhiot.mall.wholesale.activity.domain.FlashsaleGoods;
import com.lhiot.mall.wholesale.activity.domain.gridparam.ActivityGirdParam;

@Mapper
public interface FlashsaleMapper {

    int insert(List<FlashActivity> list);

    int update(FlashActivity flashGoodsParam);

    void removeInbatch(List<Long> ids);
    
    FlashActivity select(Long id);
    
    List<FlashsaleGoods> search(Long activityId);
    //分页查询分类
    List<FlashsaleGoods> pageQuery(ActivityGirdParam param);
    //查询分类的总记录数
    int pageQueryCount(ActivityGirdParam param);
    
    //统计抢购商品数量
    int flashGoodsRecord(Long activityId);
    
    //统计用户的抢购商品数量
    int userRecord(Map<String,Object> map);
}
