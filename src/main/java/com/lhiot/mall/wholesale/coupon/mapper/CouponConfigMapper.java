package com.lhiot.mall.wholesale.coupon.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.coupon.domain.ActivityCoupon;
import com.lhiot.mall.wholesale.coupon.domain.CouponConfig;
import com.lhiot.mall.wholesale.coupon.domain.gridparam.CouponGridParam;

@Mapper
public interface CouponConfigMapper {

    int insert(CouponConfig couponConfig);

    int update(CouponConfig CouponConfig);

    void removeInbatch(List<Long> ids);

    CouponConfig select(long id);
    
    //根据ids批量查询优惠券配置信息
    List<CouponConfig> search(List<Long> ids);
    
    //分页查询分类
    List<CouponConfig> pageQuery(CouponGridParam couponGridParam);
    //查询分类的总记录数
    int pageQueryCount(CouponGridParam couponGridParam);
    
    //查询活动配置优惠券
    List<ActivityCoupon> activityCoupon(Long activityId);
}
