package com.lhiot.mall.wholesale.order.api;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.lhiot.mall.wholesale.base.DateFormatUtil;
import com.lhiot.mall.wholesale.base.JacksonUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.order.domain.*;
import com.lhiot.mall.wholesale.order.domain.gridparam.OrderGridParam;
import com.lhiot.mall.wholesale.setting.domain.ParamConfig;
import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.leon.microx.common.exception.ServiceException;
import com.leon.microx.common.wrapper.ArrayObject;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;
import com.lhiot.mall.wholesale.order.service.DebtOrderService;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.setting.service.SettingService;
import com.lhiot.mall.wholesale.user.service.SalesUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;

@Api(description ="订单接口")
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderApi {

    private final OrderService orderService;
    private final DebtOrderService debtOrderService;
    private final SalesUserService salesUserService;
    private final SettingService settingService;

    @Autowired
    public OrderApi(OrderService orderService, DebtOrderService debtOrderService, SalesUserService salesUserService, SettingService settingService) {
        this.orderService = orderService;
        this.debtOrderService=debtOrderService;
        this.salesUserService=salesUserService;
        this.settingService = settingService;
    }

    @PostMapping("/myOrders/{userId}")
    @ApiOperation(value = "我的订单列表")
    public ResponseEntity<ArrayObject> queryMyOrders(@PathVariable("userId") long userId
            , @RequestParam(required = false) String settlementType, @RequestParam(required = false) String payStatus,@RequestParam String orderStatus){
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setUserId(userId);
        orderDetail.setSettlementType(settlementType);
        orderDetail.setPayStatus(payStatus);
        orderDetail.setOrderStatus(orderStatus);
        List<OrderDetail> orderDetailList = orderService.searchOrders(orderDetail);
        if (orderDetailList.isEmpty()){
            return ResponseEntity.ok(ArrayObject.of(new ArrayList<OrderDetail>()));
        }else {
            for (OrderDetail order:orderDetailList){
                String checkStatus = orderService.searchOutstandingAccountsOrder(order.getOrderCode());
                order.setCheckStatus(checkStatus);
                List<OrderGoods> goods = orderService.searchOrderGoods(order.getId());
                order.setOrderGoodsList(goods);
            }
        }
        return ResponseEntity.ok(ArrayObject.of(orderDetailList));
    }


    @GetMapping("/myOrder/{orderCode}")
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

    @GetMapping("/{id}")
    @ApiOperation(value = "根据订单编号查询订单详情")
    public ResponseEntity<OrderDetail> queryOrderById(@PathVariable("id") long id){
        OrderDetail orderDetail = orderService.searchOrderById(id);
        if (Objects.isNull(orderDetail)){
            throw new ServiceException("没有该订单信息");
        }
        List<OrderGoods> goods = orderService.searchOrderGoods(id);
        if (goods.isEmpty()){
            orderDetail.setOrderGoodsList(new ArrayList<OrderGoods>());
        }else {
            orderDetail.setOrderGoodsList(goods);
        }
        return ResponseEntity.ok(orderDetail);
    }

    @GetMapping("/invoice/orders/{userId}")
    @ApiOperation(value = "查询可开发票的订单列表")
    public ResponseEntity<ArrayObject> invoiceOrders(@PathVariable("userId") @NotNull long userId,
                                                     @RequestParam String payStatus,@RequestParam String orderStatus) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setUserId(userId);
        orderDetail.setPayStatus(payStatus);
        orderDetail.setOrderStatus(orderStatus);
        List <OrderDetail> orders = orderService.searchAfterSaleOrder(orderDetail);
        List<OrderDetail> orderResults=new ArrayList<OrderDetail>();
        if (orders.isEmpty()){
            return ResponseEntity.ok(ArrayObject.of(new ArrayList<>()));
        }
        String time = DateFormatUtil.format1(new java.util.Date());
        Timestamp currentTime = Timestamp.valueOf(time);
        for (OrderDetail order:orders) {
            //发票订单是收货已付款且过售后时间的订单
            if ("received".equals(order.getOrderStatus())&&"paid".equals(order.getPayStatus())&&order.getAfterSaleTime().before(currentTime)){
                List<OrderGoods> goods = orderService.searchOrderGoods(order.getId());
                if (goods.isEmpty()){
                    orderDetail.setOrderGoodsList(new ArrayList<OrderGoods>());
                }else {
                    orderDetail.setOrderGoodsList(goods);
                }
                orderResults.add(order);
            }
        }
        return ResponseEntity.ok(ArrayObject.of(orderResults));
    }

    @GetMapping("/order/distribution/{fee}")
    @ApiOperation(value = "查询配送费")
    public  ResponseEntity<Integer> distribution(@PathVariable("fee") @NotNull Integer fee) throws Exception{
        ParamConfig paramConfig = settingService.searchConfigParam("distributionFeeSet");
        String distribution = paramConfig.getConfigParamValue();
        Distribution[] distributionsJson = JacksonUtils.fromJson(distribution,  Distribution[].class);//字符串转json
        //[{"minPrice": 200,"maxPrice":300,"distributionFee": 25},
        // {"minPrice": 300,"maxPrice": 500,"distributionFee": 15},
        // {"minPrice":500,"maxPrice": 1000,"distributionFee": 0}]
        for (Distribution item:distributionsJson){
            if (fee>=item.getMinPrice()&&fee<item.getMaxPrice()){
                Integer distributionFee = item.getDistributionFee();
                return ResponseEntity.ok(distributionFee);
            }
        }
        return ResponseEntity.ok(100);
    }

    @GetMapping("/orders/after-sale/{userId}")
    @ApiOperation(value = "查询售后订单")
    public ResponseEntity<ArrayObject> queryAfterSale(@PathVariable("userId") @NotNull long userId
            ,@RequestParam String orderStatus, @RequestParam String payStatus) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setUserId(userId);
        orderDetail.setOrderStatus(orderStatus);
        orderDetail.setPayStatus(payStatus);
        List <OrderDetail> orders = orderService.searchAfterSaleOrder(orderDetail);
        if (orders.isEmpty()){
            return ResponseEntity.ok(ArrayObject.of(new ArrayList<>()));
        }
        String time = DateFormatUtil.format1(new java.util.Date());
        Timestamp currentTime = Timestamp.valueOf(time);
        for (OrderDetail order:orders) {
            List<OrderGoods> goods = orderService.searchOrderGoods(order.getId());
            if (goods.isEmpty()){
                orderDetail.setOrderGoodsList(new ArrayList<OrderGoods>());
            }else {
                orderDetail.setOrderGoodsList(goods);
            }
        }
        return ResponseEntity.ok(ArrayObject.of(orders));
    }

    @PostMapping("/create")
    @ApiOperation(value = "创建订单")
    public ResponseEntity<Integer> create(@RequestBody OrderDetail orderDetail){

        SalesUserRelation salesUserRelation=new SalesUserRelation();
        salesUserRelation.setUserId(orderDetail.getUserId());
        salesUserRelation.setCheck(1);//通过审核
        SalesUserRelation salesUserRelationResult=salesUserService.searchSaleRelationship(salesUserRelation);
        if(Objects.nonNull(salesUserRelationResult)){
            //设置订单业务员编码
            orderDetail.setSalesmanId(salesUserRelationResult.getSalesmanId());
        }
        Integer result = orderService.create(orderDetail);
        //FIXME 创建的时候发送创建广播消息 用于优惠券设置无效
        //fixme mq设置三十分钟失效

        if(orderDetail.getSettlementType()== ""){  //FIXME 改为枚举 if(orderDetail.getSettlementType()==1){
            DebtOrder debtOrder=new DebtOrder();
            //FIXME 需要赋值
            debtOrderService.create(debtOrder);
            //FIXME 改为枚举    orderDetail.setOrderStatus(3);//待收货
            orderDetail.setOrderStatus("");//待收货
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/cancel/unpay/{orderCode}")
    @ApiOperation(value = "取消未支付订单")
    public ResponseEntity<Integer> cancelUnpayOrder(@PathVariable("orderCode") String orderCode){
        Integer result=orderService.cancelUnpayOrder(orderCode);
        return ResponseEntity.ok(result);
    }


    @PutMapping("/cancel/payed/{orderCode}")
    @ApiOperation(value = "取消待收货订单")
    public ResponseEntity<Integer> cancelPayedOrder(@PathVariable("orderCode") String orderCode){
        //判断订单类型来确定退款方式
        //需求 当天可以自己任意取消支付订单，订单为全部商品,最后取消海鼎订单 超过指定时间，需要后台审核订单，审核走售后流程
        OrderDetail orderDetail = orderService.searchOrder(orderCode);
        if (Objects.isNull(orderDetail)){
            throw new ServiceException("没有该订单信息");
        }
        //FIXME 改为枚举     if(orderDetail.getOrderStatus()!=3){
        if(orderDetail.getOrderStatus()!=""){
            throw new ServiceException("非待收货订单状态");
        }
        //FIXME 改为枚举      if(orderDetail.getPayStatus()!=0){
        if(orderDetail.getPayStatus()!=""){
            throw new ServiceException("订单未支付");
        }
       return ResponseEntity.ok(orderService.cancelPayedOrder(orderDetail));
    }

    @PostMapping("/grid")
    @ApiOperation(value = "后台管理-分页查询订单信息", response = PageQueryObject.class)
    public ResponseEntity<PageQueryObject> grid(@RequestBody(required = true) OrderGridParam param) throws IntrospectionException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return ResponseEntity.ok(orderService.pageQuery(param));
    }

    @GetMapping("/detail/{id}")
    @ApiOperation(value = "后台管理-根据订单id查看订单详情",response = OrderGridResult.class)
    public  ResponseEntity<OrderDetail> demandGoodsDetail(@PathVariable("id") Long id){
        return ResponseEntity.ok(orderService.detail(id));
    }

}
