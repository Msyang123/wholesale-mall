package com.lhiot.mall.wholesale.order.mapper;

import com.lhiot.mall.wholesale.order.domain.*;
import com.lhiot.mall.wholesale.order.domain.gridparam.OrderGridParam;
import com.lhiot.mall.wholesale.user.domain.SalesUserPerformanceDetail;
import com.lhiot.mall.wholesale.user.domain.gridparam.UserPerformanceGridParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;


@Mapper
public interface OrderMapper {
    List<OrderDetail> searchOrders(OrderDetail orderDetail);

    List<OrderDetail> searchOrdersByOrderCodes(List<String> orderCodes);

    List<OrderDetail> searchOrdersByOrderIds(List ids);

    List<OrderGoods> searchOrderGoods(Long orderId);

    String searchOutstandingAccountsOrder(String orderCode);

    OrderDetail searchOrder(String orderCode);

    OrderDetail searchOrderById(Long orderId);

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
    Long save(OrderDetail orderDetail);

    /**
     * 保存订单商品
     * @param orderGoods
     * @return
     */
    Integer saveOrderGoods(List<OrderGoods> orderGoods);

    /**
     * 依据订单号修改订单状态
     * @return
     */
    Integer updateOrderStatusByCode(OrderDetail orderDetail);

    /**
     * 依据订单码修改订单信息
     * @return
     */
    Integer updateOrder(OrderDetail orderDetail);

    /**
     * 依据订单号修改订单信息
     * @return
     */
    Integer updateOrderById(OrderDetail orderDetail);


    //统计商品的售卖数量，根据商品ids
    List<SoldQuantity> soldQuantity(List<Long> goodsIds);

    OrderDetail order(OrderDetail orderDetail);

    List<OrderDetail> lateOrders(OrderParam orderParam);

    OrderDetail lateOneOrder(Long userId);

    Integer lateOrdersFee(OrderParam orderParam);

    OrderDetail select(Long id);

    OrderDetail userOrder(OrderParam orderParam);

    //判断门店是否下过单
    Integer isExistsOrderByuserId(Long userId);

    Map<String,Object> countPayAbleFee(Map<String, Object> param);

    Map<String,Object> countOverDue(Map<String, Object> param);

    OrderDetail countFee(List<String> orderCode);
}
