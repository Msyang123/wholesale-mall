package com.lhiot.mall.wholesale.aftersale.mapper;

import com.lhiot.mall.wholesale.aftersale.domain.OrderRefundApplication;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderRefundApplicationMapper {


    int create(OrderRefundApplication orderRefundApplication);

    int updateById(OrderRefundApplication orderRefundApplication);

    List<OrderRefundApplication> orderRefundApplicationList(long userId);
}
