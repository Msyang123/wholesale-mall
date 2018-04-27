package com.lhiot.mall.wholesale.coupon.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.coupon.domain.CouponEntity;
import com.lhiot.mall.wholesale.coupon.domain.CouponEntityParam;
import com.lhiot.mall.wholesale.coupon.domain.gridparam.CouponGridParam;

@Mapper
public interface CouponEntityMapper {

    int insert(CouponEntityParam couponEntityParam);

    int insertBatch(List<CouponEntityParam> couponEntityParams);
    
    int update(CouponEntityParam couponEntityParam);
    
    void removeInbatch(List<Long> ids);
    
    CouponEntity select(long id);
    
    //分页查询分类
    List<CouponEntity> pageQuery(CouponGridParam couponGridParam);
    //查询分类的总记录数
    int pageQueryCount(CouponGridParam couponGridParams);
}
