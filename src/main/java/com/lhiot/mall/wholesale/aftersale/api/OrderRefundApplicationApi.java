package com.lhiot.mall.wholesale.aftersale.api;

import com.leon.microx.common.wrapper.ArrayObject;
import com.lhiot.mall.wholesale.aftersale.domain.OrderRefundApplication;
import com.lhiot.mall.wholesale.aftersale.service.OrderRefundApplicationService;
import com.lhiot.mall.wholesale.base.DataMergeUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;
import com.lhiot.mall.wholesale.order.domain.OrderGridResult;
import com.lhiot.mall.wholesale.order.domain.gridparam.OrderGridParam;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


@Api(description = "订单售后接口")
@Slf4j
@RestController
@RequestMapping("/after-sale")
public class OrderRefundApplicationApi {

    private final OrderRefundApplicationService orderRefundApplicationService;

    private final OrderService orderService;

    private final UserService userService;

    @Autowired
    public OrderRefundApplicationApi(OrderRefundApplicationService orderRefundApplicationService, OrderService orderService, UserService userService) {
        this.orderRefundApplicationService = orderRefundApplicationService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping("/apply")
    @ApiOperation(value = "售后申请")
    public ResponseEntity apply(@RequestBody OrderRefundApplication orderRefundApplication) {
        return ResponseEntity.ok(orderRefundApplicationService.create(orderRefundApplication));
    }

    @PutMapping("/verify")
    @ApiOperation(value = "售后审核")
    public ResponseEntity verify(@RequestBody OrderRefundApplication orderRefundApplication) {
        return ResponseEntity.ok(orderRefundApplicationService.updateById(orderRefundApplication));
    }

    @GetMapping("/list/{userId}")
    @ApiOperation(value = "售后申请列表")
    public ResponseEntity<ArrayObject> orderRefundApplicationList(@PathVariable("userId") Long userId) {
        List<OrderRefundApplication> orderRefundApplicationList = orderRefundApplicationService.orderRefundApplicationList(userId);
        for (OrderRefundApplication orderRefund : orderRefundApplicationList) {
            OrderDetail orderDetail = orderService.searchOrder(orderRefund.getOrderId());
             List<OrderGoods> orderGoodsList =orderService.searchOrderGoods(orderDetail.getId());
            orderRefund.setOrderCreateTime(orderDetail.getCreateTime());
            orderRefund.setOrderGoodsList(orderGoodsList);
        }
        return ResponseEntity.ok(ArrayObject.of(orderRefundApplicationList));
    }

    /***************************************后台管理系统*************************************************/
    @PostMapping("/grid")
    @ApiOperation(value = "后台管理-分页查询售后订单信息", response = PageQueryObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) OrderGridParam param) throws IntrospectionException, InstantiationException, IllegalAccessException, InvocationTargetException {
        OrderRefundApplication orderRefundApplication=new OrderRefundApplication();
        List<OrderRefundApplication> orderRefundApplicationList = orderRefundApplicationService.list(orderRefundApplication);
        List<String> orderDetailList = new ArrayList<String>();
        List<String> statuss = new ArrayList<String>();
        for (OrderRefundApplication item:orderRefundApplicationList) {
            orderDetailList.add(item.getOrderId());
            statuss.add(item.getAuditStatus());
        }
        param.setOrderIds(orderDetailList);
        param.setAuditStatuss(statuss);
        return ResponseEntity.ok(orderService.pageQuery(param));
    }
}
