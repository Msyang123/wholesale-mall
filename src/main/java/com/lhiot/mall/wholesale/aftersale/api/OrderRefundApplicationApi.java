package com.lhiot.mall.wholesale.aftersale.api;

import com.leon.microx.common.wrapper.ArrayObject;
import com.lhiot.mall.wholesale.aftersale.domain.OrderRefundApplication;
import com.lhiot.mall.wholesale.aftersale.domain.OrderResult;
import com.lhiot.mall.wholesale.aftersale.service.OrderRefundApplicationService;
import com.lhiot.mall.wholesale.base.DateFormatUtil;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;
import com.lhiot.mall.wholesale.order.domain.OrderGridResult;
import com.lhiot.mall.wholesale.order.domain.gridparam.OrderGridParam;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Api(description = "订单售后接口")
@Slf4j
@RestController
@RequestMapping("/after-sale")
public class OrderRefundApplicationApi {

    private final OrderRefundApplicationService orderRefundApplicationService;

    private final OrderService orderService;

    private final RedissonClient redissonClient;

    @Autowired
    public OrderRefundApplicationApi(OrderRefundApplicationService orderRefundApplicationService, OrderService orderService, RedissonClient redissonClient) {
        this.orderRefundApplicationService = orderRefundApplicationService;
        this.orderService = orderService;
        this.redissonClient = redissonClient;
    }

    @PostMapping("/apply")
    @ApiOperation(value = "售后申请")
    public ResponseEntity apply(@RequestBody OrderRefundApplication orderRefundApplication) {
        RMapCache<String,Object> cache=  redissonClient.getMapCache("afterSale");
        if(Objects.nonNull(cache.get("orderId"+orderRefundApplication.getOrderId()))){
            return ResponseEntity.badRequest().body("申请正在提交中，请不要重复操作");
        }
        // 发送中，将用户信息 缓存起来
        cache.put("orderId"+orderRefundApplication.getOrderId(),
                orderRefundApplication.getOrderId(),20, TimeUnit.SECONDS);
        String time = DateFormatUtil.format1(new java.util.Date());
        Timestamp currentTime = Timestamp.valueOf(time);
        orderRefundApplication.setOrderCreateTime(currentTime);
        orderRefundApplication.setAuditStatus("unaudited");
        if (orderRefundApplicationService.create(orderRefundApplication)>0){
            return ResponseEntity.ok().body("提交成功");
        }
        return ResponseEntity.badRequest().body("提交失败");
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
           // orderRefund.setOrderCreateTime(orderDetail.getCreateTime());
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
        List<Map> statuss = new ArrayList<Map>();
        if (!orderRefundApplicationList.isEmpty()){
            for (OrderRefundApplication item:orderRefundApplicationList) {
                orderDetailList.add(item.getOrderId());
                //statuss.add(item.getAuditStatus());
                Map map = new HashMap();
                map.put("orderCode",item.getOrderId());
                map.put("status",item.getAuditStatus());
                statuss.add(map);
            }
        }
        param.setOrderIds(orderDetailList);
        param.setAuditStatuss(statuss);
        return ResponseEntity.ok(orderRefundApplicationService.pageQuery(param));
    }

    @GetMapping("/detail/{id}")
    @ApiOperation(value = "后台管理-根据订单id查看售后订单详情",response = OrderGridResult.class)
    public  ResponseEntity<OrderResult> demandGoodsDetail(@PathVariable("id") Long id){
        return ResponseEntity.ok(orderRefundApplicationService.detail(id));
    }

    @PostMapping("/update/{id}")
    @ApiOperation(value = "依据id更新售后申请表")
    public ResponseEntity updateOrderRefund(@PathVariable("id") Long id,@RequestBody OrderRefundApplication orderRefundApplication) {
        orderRefundApplication.setId(id);
        if (orderRefundApplicationService.updateById(orderRefundApplication)>0){
            return ResponseEntity.ok().body("申请成功");
        }
        return ResponseEntity.badRequest().body("申请失败");
    }
}
