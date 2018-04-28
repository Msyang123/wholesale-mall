package com.lhiot.mall.wholesale.coupon.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.coupon.domain.CouponEntityResult;
import com.lhiot.mall.wholesale.coupon.domain.CouponEntity;
import com.lhiot.mall.wholesale.coupon.domain.UserCouponParam;
import com.lhiot.mall.wholesale.coupon.domain.gridparam.CouponGridParam;

@Mapper
public interface CouponEntityMapper {

    int insert(CouponEntity couponEntityParam);

    int insertBatch(List<CouponEntity> couponEntityParams);
    
    int update(CouponEntity couponEntityParam);
    
    void removeInbatch(List<Long> ids);
    
    CouponEntityResult select(long id);
    
    //分页查询分类
    List<CouponEntityResult> pageQuery(CouponGridParam couponGridParam);
    //查询分类的总记录数
    int pageQueryCount(CouponGridParam couponGridParams);
    //查询用户的优惠券列表
    List<CouponEntity> searchByUser(UserCouponParam userCouponParam);
}
