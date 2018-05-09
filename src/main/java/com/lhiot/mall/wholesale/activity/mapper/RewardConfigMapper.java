package com.lhiot.mall.wholesale.activity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.activity.domain.RewardConfig;
import com.lhiot.mall.wholesale.activity.domain.RewardCoupon;
import com.lhiot.mall.wholesale.activity.domain.gridparam.RewardConfigGridParam;

@Mapper
public interface RewardConfigMapper {

    int insert(List<RewardConfig> rewardConfig);

    int update(RewardConfig rewardConfig);
    
    void removeInbatch(List<Long> ids);

    RewardCoupon select(Long id);
    
    List<RewardCoupon> selectByActivity(Long activityId);
    
    //分页查询分类
    List<RewardCoupon> pageQuery(RewardConfigGridParam rewardConfigGridParam);
    //查询分类的总记录数
    int pageQueryCount(RewardConfigGridParam rewardConfigGridParam);
}
