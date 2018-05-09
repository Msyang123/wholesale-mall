package com.lhiot.mall.wholesale.order.mapper;

import com.lhiot.mall.wholesale.order.domain.*;
import com.lhiot.mall.wholesale.order.domain.gridparam.OrderGridParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;


@Mapper
public interface OrderMapper {
    List<OrderDetail> searchOrders(OrderDetail orderDetail);

    List<OrderDetail> searchOrdersByOrderCodes(List<String> orderCodes);
    List<OrderGoods> searchOrderGoods(long orderId);

    String searchOutstandingAccountsOrder(String orderCode);

    OrderDetail searchOrder(String orderCode);

/*    OrderDetail searchOrderById(long orderId);*/

    /**
     * 后台管理--分页查询订单
     * @param param
     * @return
     */
    List<OrderGridResult> pageQuery(OrderGridParam param);


    /**
     * 后台管理--查询分类的总记录数
     * @param param
     * @return
     */
    int pageQueryCount(OrderGridParam param);

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

    /**
     * 依据订单号修改订单信息
     * @return
     */
    int updateOrder(OrderDetail orderDetail);


    //统计商品的售卖数量，根据商品ids
    List<SoldQuantity> soldQuantity(List<Long> goodsIds);

    OrderDetail order(OrderDetail orderDetail);

    List<OrderDetail> lateOrders(OrderParam orderParam);

    OrderDetail lateOneOrder(long userId);

    Integer lateOrdersFee(OrderParam orderParam);

    OrderDetail select(long id);

    OrderDetail userOrder(OrderParam orderParam);

}
