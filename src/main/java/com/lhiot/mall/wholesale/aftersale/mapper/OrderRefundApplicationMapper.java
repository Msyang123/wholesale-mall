package com.lhiot.mall.wholesale.aftersale.mapper;

import com.lhiot.mall.wholesale.aftersale.domain.OrderRefundApplication;
import com.lhiot.mall.wholesale.aftersale.domain.OrderResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderRefundApplicationMapper {

    Integer create(OrderRefundApplication orderRefundApplication);

    Integer updateById(OrderRefundApplication orderRefundApplication);

    List<OrderRefundApplication> orderRefundApplicationList(OrderRefundApplication orderRefundApplication);

    List<OrderRefundApplication> list(OrderRefundApplication orderRefundApplication);

    OrderRefundApplication refundInfo(OrderRefundApplication orderRefundApplication);

    OrderResult searchOrderById(long orderId);

}
