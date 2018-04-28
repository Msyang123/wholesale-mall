package com.lhiot.mall.wholesale.order.api;

import com.leon.microx.common.exception.ServiceException;
import com.leon.microx.common.wrapper.ArrayObject;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.demand.domain.DemandGoodsResult;
import com.lhiot.mall.wholesale.demand.domain.gridparam.DemandGoodsGridParam;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;
import com.lhiot.mall.wholesale.order.domain.gridparam.OrderGridParam;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.user.domain.UserAddress;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Api
@Slf4j
@RestController
public class OrderApi {

    private final OrderService orderService;

    @Autowired
    public OrderApi(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order/myOrders/{userId}")
    @ApiOperation(value = "我的订单列表")
    public ResponseEntity<ArrayObject> queryMyOrders(@PathVariable("userId") long userId
            , @RequestParam(required = false) Integer payType, @RequestParam(required = false) Integer payStatus,@RequestParam Integer orderStatus){
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setUserId(userId);
        orderDetail.setOrderType(payType);
        orderDetail.setPayStatus(payStatus);
        orderDetail.setOrderStatus(orderStatus);
        List<OrderDetail> orderDetailList = orderService.searchOrders(orderDetail);
        if (orderDetailList.isEmpty()){
            return ResponseEntity.ok(ArrayObject.of(new ArrayList<OrderDetail>()));
        }else {
            for (OrderDetail order:orderDetailList){
                Integer checkStatus = orderService.searchOutstandingAccountsOrder(order.getOrderCode());
                order.setCheckStatus(checkStatus);
                List<OrderGoods> goods = orderService.searchOrderGoods(order.getId());
                order.setOrderGoodsList(goods);
            }
        }

        return ResponseEntity.ok(ArrayObject.of(orderDetailList));
    }


    @PostMapping("/order/myOrder/{orderCode}")
    @ApiOperation(value = "根据订单编号查询订单详情")
    public ResponseEntity<OrderDetail> queryOrder(@PathVariable("orderCode") String orderCode){
        OrderDetail orderDetail = orderService.searchOrder(orderCode);
        if (Objects.isNull(orderDetail)){
            throw new ServiceException("没有该订单信息");
        }
        List<OrderGoods> goods = orderService.searchOrderGoods(orderDetail.getId());
        if (goods.isEmpty()){
            orderDetail.setOrderGoodsList(new ArrayList<OrderGoods>());
        }else {
            orderDetail.setOrderGoodsList(goods);
        }
        return ResponseEntity.ok(orderDetail);
    }

   /* @PostMapping("/order/myOrder/grid")
    @ApiOperation(value = "新建一个查询，分页查询新品需求", response = PageQueryObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) OrderGridParam param) {
        return ResponseEntity.ok(orderService.pageQuery(param));
    }

    @GetMapping("/demandgoods/detail/{id}")
    @ApiOperation(value = "新品需求详情页面",response = DemandGoodsResult.class)
    public  ResponseEntity<DemandGoodsResult> demandGoodsDetail(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.detail(id));
    }*/
}
