package com.lhiot.mall.wholesale.order.mapper;

import com.lhiot.mall.wholesale.demand.domain.DemandGoodsResult;
import com.lhiot.mall.wholesale.demand.domain.gridparam.DemandGoodsGridParam;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;
import com.lhiot.mall.wholesale.order.domain.OrderGridResult;
import com.lhiot.mall.wholesale.order.domain.gridparam.OrderGridParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    List<OrderDetail> searchOrders(OrderDetail orderDetail);

    List<OrderGoods> searchOrderGoods(long orderId);

    Integer searchOutstandingAccountsOrder(String orderCode);

    OrderDetail searchOrder(String orderCode);

    //后台管理--分页查询新品需求
    List<OrderGridResult> pageQuery(OrderGridParam param);

    //后台管理--查询分类的总记录数
    int pageQueryCount(OrderGridParam param);
}
