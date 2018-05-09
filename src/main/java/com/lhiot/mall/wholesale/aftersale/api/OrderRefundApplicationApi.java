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
    @GetMapping("/page")
    @ApiOperation(value = "分页查询售后申请表列表")
    public ResponseEntity<ArrayObject> list(
            @RequestParam(value="id", required = false) Long id,
            @RequestParam(value="auditStatus", required = false) String auditStatus,
            @RequestParam(value="orderDiscountFee", required = false) Integer orderDiscountFee,
            @RequestParam(value="orderId", required = false) Long orderId,
            @RequestParam(value="phone", required = false) String phone,
            @RequestParam(value="rows", required = false, defaultValue="10") Integer rows,
            @RequestParam(value="page", required = false, defaultValue="1") Integer page,
            @RequestParam(value="sidx", required = false, defaultValue="") String sidx,
            @RequestParam(value="sord", required = false, defaultValue="") String sord) throws IntrospectionException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Map<String,Object> param=new HashMap<>();
        param.put("id",id);
        param.put("auditStatus",auditStatus);
        param.put("orderDiscountFee",orderDiscountFee);
        param.put("orderId",orderId);
        param.put("page",page);
        param.put("rows",rows);
        param.put("sidx",sidx);
        param.put("sord",sord);
        OrderRefundApplication orderRefundApplication=new OrderRefundApplication();
        List<OrderRefundApplication> orderRefundApplicationList = orderRefundApplicationService.list(orderRefundApplication);
        List<String> orderDetailList = new ArrayList<String>();
        for (OrderRefundApplication item:orderRefundApplicationList) {
            orderDetailList.add(item.getOrderId());
        }
        OrderGridParam orderParam = new OrderGridParam();
        orderParam.setOrderIds(orderDetailList);
        List<OrderGridResult> orderGridResults = orderService.orderGridData(orderParam);
        User userParam = new User();
        userParam.setPhone(phone);
        List<User> users = userService.searchByPhoneOrName(userParam);
        for (OrderGridResult orderGridResult:orderGridResults) {
            for (User user:users) {
                if (Objects.equals(orderGridResult.getUserId(), user.getId())) {
                    orderGridResult.setPhone(user.getPhone());
                    orderGridResult.setShopName(user.getShopName());
                    orderGridResult.setUserName(user.getUserName());
                    orderGridResult.setCreateTime(orderGridResult.getCreateTime());
                    break;
                }
            }
        }
        Map<String,Object> param2=new HashMap<>();
        param.put("page",page);
        param.put("rows",rows);
        param.put("sidx",sidx);
        param.put("sord",sord);
        return ResponseEntity.ok(ArrayObject.of(orderGridResults));
    }
}
