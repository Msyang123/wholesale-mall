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

    List<OrderDetail> searchOrdersByOrderCodes(List<String> orderCodes);
    List<OrderGoods> searchOrderGoods(long orderId);

    Integer searchOutstandingAccountsOrder(String orderCode);

    OrderDetail searchOrder(String orderCode);

<<<<<<< HEAD
    //后台管理--分页查询新品需求
    List<OrderGridResult> pageQuery(OrderGridParam param);

    //后台管理--查询分类的总记录数
    int pageQueryCount(OrderGridParam param);
=======
    List<OrderDetail> searchAfterSaleOrders(OrderDetail orderDetail);

    /**
     * 保存订单信息
     * @param orderDetail
     * @return
     */
    long save(OrderDetail orderDetail);

    /**
     * 保存订单商品
     * @param orderGoods
     * @return
     */
    int saveOrderGoods(List<OrderGoods> orderGoods);

    /**
     * 依据订单号修改订单状态
     * @return
     */
    int updateOrderStatusByCode(OrderDetail orderDetail);
>>>>>>> sgsl/master
}
