package com.lhiot.mall.wholesale.order.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;
import com.lhiot.mall.wholesale.order.domain.OrderGridResult;
import com.lhiot.mall.wholesale.order.domain.OrderParam;
import com.lhiot.mall.wholesale.order.domain.SoldQuantity;
import com.lhiot.mall.wholesale.order.domain.gridparam.OrderGridParam;
import com.lhiot.mall.wholesale.user.domain.Achievement;
import com.lhiot.mall.wholesale.user.domain.SaleStatisticsParam;


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

    List<OrderDetail> unDeliveryOrders(OrderDetail orderDetail);

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


    //统计商品的售卖数量，根据商品规格ids
    List<SoldQuantity> soldQuantity(List<Long> standardIds);

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

    //后台管理系统 查询订单应付、实付、优惠费用之和
    OrderDetail countFee(List<String> orderId);

    //后台管理系统 导出订单
    List<Map<String, Object>> exportData(OrderGridParam param);

    //后台管理系统 导出订单
    List<Map<String, Object>> exportDataOrderGoods(OrderGridParam param);

	//统计业务员销售总金额
	Long salseAmount(SaleStatisticsParam param);

	//统计业务员订单数和下单用户数
	Achievement orderAndUserCount(SaleStatisticsParam param);
	
	//统计退单数
	Long refundedCount(SaleStatisticsParam param);
}
