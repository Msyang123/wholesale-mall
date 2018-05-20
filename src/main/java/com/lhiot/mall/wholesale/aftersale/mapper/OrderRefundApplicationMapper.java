package com.lhiot.mall.wholesale.aftersale.mapper;

import com.lhiot.mall.wholesale.aftersale.domain.OrderRefundApplication;
import com.lhiot.mall.wholesale.aftersale.domain.OrderRefundPage;
import com.lhiot.mall.wholesale.aftersale.domain.OrderResult;
import com.lhiot.mall.wholesale.order.domain.gridparam.OrderGridParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderRefundApplicationMapper {

    Integer create(OrderRefundApplication orderRefundApplication);

    Integer updateById(OrderRefundApplication orderRefundApplication);

    List<OrderRefundApplication> orderRefundApplicationList(OrderRefundApplication orderRefundApplication);

    List<OrderRefundApplication> list(OrderRefundApplication orderRefundApplication);

    OrderRefundApplication refundInfo(OrderRefundApplication orderRefundApplication);

    OrderResult searchOrderById(String orderCode);

    Integer pageQueryCount(OrderGridParam param);

    List<OrderRefundPage> page(OrderGridParam param);

}
