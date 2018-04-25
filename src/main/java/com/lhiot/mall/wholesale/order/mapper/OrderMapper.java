package com.lhiot.mall.wholesale.order.mapper;

import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    List<OrderDetail> searchOrders(OrderDetail orderDetail);

    List<OrderGoods> searchOrderGoods(long orderId);

    Integer searchOutstandingAccountsOrder(String orderCode);

    OrderDetail searchOrder(String orderCode);
}
