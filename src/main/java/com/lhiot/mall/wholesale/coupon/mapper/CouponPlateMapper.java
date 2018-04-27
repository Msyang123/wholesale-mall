package com.lhiot.mall.wholesale.coupon.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.coupon.domain.CouponPlate;
import com.lhiot.mall.wholesale.coupon.domain.gridparam.CouponGridParam;

@Mapper
public interface CouponPlateMapper {

    int insert(CouponPlate couponPlate);

    int update(CouponPlate couponPlate);

    void removeInbatch(List<Long> ids);

    CouponPlate select(long id);
    
    //分页查询分类
    List<CouponPlate> pageQuery(CouponGridParam couponGridParam);
    //查询分类的总记录数
    int pageQueryCount(CouponGridParam couponGridParams);
}
