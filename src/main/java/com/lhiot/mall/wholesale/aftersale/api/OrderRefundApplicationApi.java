package com.lhiot.mall.wholesale.aftersale.api;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.leon.microx.common.wrapper.ArrayObject;
import com.lhiot.mall.wholesale.aftersale.domain.OrderRefundApplication;
import com.lhiot.mall.wholesale.aftersale.domain.OrderResult;
import com.lhiot.mall.wholesale.aftersale.service.OrderRefundApplicationService;
import com.lhiot.mall.wholesale.base.DateFormatUtil;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.base.StringReplaceUtil;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;
import com.lhiot.mall.wholesale.order.domain.OrderGridResult;
import com.lhiot.mall.wholesale.order.domain.gridparam.OrderGridParam;
import com.lhiot.mall.wholesale.order.service.OrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;


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
        if (Objects.isNull(orderRefundApplication.getContactsPhone())||Objects.equals(orderRefundApplication.getContactsPhone(),"")
                ||Objects.isNull(orderRefundApplication.getApplicationType())||Objects.equals(orderRefundApplication.getApplicationType(),"")
                ||Objects.isNull(orderRefundApplication.getExistProblem())||Objects.equals(orderRefundApplication.getExistProblem(),"")){
            return ResponseEntity.badRequest().body("请完善信息在提交");
        }
        String orderCode = orderRefundApplication.getOrderId();
        //判断订单是否存在
        OrderDetail orderDetail = orderService.findOrderByCode(orderCode);
        if(Objects.isNull(orderDetail)){
        	return ResponseEntity.badRequest().body("订单不存在");
        }
        //判断是否已经过了售后期
        if(!orderRefundApplicationService.withinTheTime(orderDetail.getReceiveTime())){
        	return ResponseEntity.badRequest().body("订单已过售后期限");
        }
        //判断当前订单是已经售后
        if(orderRefundApplicationService.hasApply(orderCode)){
        	return ResponseEntity.badRequest().body("已经申请过售后，请勿重复申请");
        }
        boolean isMoblieNo = StringReplaceUtil.isMobileNO(orderRefundApplication.getContactsPhone());
        if (!isMoblieNo){
            return ResponseEntity.badRequest().body("手机号码格式错误");
        }
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

    @PostMapping("/apply/supplement")
    @ApiOperation(value = "新增补差价记录")
    public ResponseEntity<?> applySupplement(@RequestBody OrderRefundApplication orderRefundApplication){
    	int supplement = orderRefundApplication.getOrderDiscountFee();
    	if(supplement < 0){
    		return ResponseEntity.badRequest().body("金额错误");
    	}
    	boolean success = orderRefundApplicationService.create(orderRefundApplication) > 0;
    	if(success){
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
    public ResponseEntity<ArrayObject> orderRefundApplicationList(@PathVariable("userId") Long userId,@RequestParam(defaultValue="1") Integer page,
                                                                  @RequestParam(defaultValue="10") Integer rows) {
        OrderRefundApplication orderRefundApplication = new OrderRefundApplication();
        orderRefundApplication.setUserId(userId);
        orderRefundApplication.setPage(page);
        orderRefundApplication.setRows(rows);
        orderRefundApplication.setStart((page-1)*rows);
        //设置默认排序方式，未审核的并时间倒序
        orderRefundApplication.setSidx("audit_status desc,create_at");
        orderRefundApplication.setSord("desc");
        
        List<OrderRefundApplication> orderRefundApplicationList = orderRefundApplicationService.orderRefundApplicationList(orderRefundApplication);
        if (orderRefundApplicationList.isEmpty()){
            ResponseEntity.ok(ArrayObject.of(new ArrayList<OrderRefundApplication>()));
        }
        for (OrderRefundApplication orderRefund : orderRefundApplicationList) {
            OrderDetail orderDetail = orderService.searchOrder(orderRefund.getOrderId());
            List<OrderGoods> orderGoodsList =orderService.searchOrderGoods(orderDetail.getId());
            // orderRefund.setOrderCreateTime(orderDetail.getCreateTime());
            Integer payableFee = Objects.isNull(orderDetail.getPayableFee())?0:orderDetail.getPayableFee();
            Integer deliveryFee = Objects.isNull(orderDetail.getDeliveryFee())?0:orderDetail.getDeliveryFee();
            orderRefund.setOrderGoodsList(orderGoodsList);
            orderRefund.setNeedPay(payableFee + deliveryFee);
        }
        return ResponseEntity.ok(ArrayObject.of(orderRefundApplicationList));
    }

    @GetMapping("/detail/{orderCode}/{userId}")
    @ApiOperation(value = "售后订单详情接口")
    public ResponseEntity orderRefundResult(@PathVariable("orderCode") String orderCode,@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(orderRefundApplicationService.orderRefundResult(orderCode.trim(),userId));
    }
    /***************************************后台管理系统*************************************************/
    @PostMapping("/grid")
    @ApiOperation(value = "后台管理-分页查询售后订单信息", response = PageQueryObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) OrderGridParam param) throws IntrospectionException, InstantiationException, IllegalAccessException, InvocationTargetException {
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
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("申请失败");
    }
}
