package com.lhiot.mall.wholesale.aftersale.service;

import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.aftersale.domain.OrderRefundApplication;
import com.lhiot.mall.wholesale.aftersale.domain.OrderRefundPage;
import com.lhiot.mall.wholesale.aftersale.domain.OrderResult;
import com.lhiot.mall.wholesale.aftersale.mapper.OrderRefundApplicationMapper;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.goods.domain.Goods;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;
import com.lhiot.mall.wholesale.order.domain.OrderGridResult;
import com.lhiot.mall.wholesale.order.domain.gridparam.OrderGridParam;
import com.lhiot.mall.wholesale.order.mapper.OrderMapper;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.pay.service.PaymentLogService;
import com.lhiot.mall.wholesale.user.domain.SalesUser;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.service.SalesUserService;
import com.lhiot.mall.wholesale.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class OrderRefundApplicationService {

    private final OrderRefundApplicationMapper orderRefundApplicationMapper;

    private final OrderMapper orderMapper;

    private final UserService userService;

    private final PaymentLogService paymentLogService;

    private final SalesUserService salesUserService;

    @Autowired
    public OrderRefundApplicationService(OrderRefundApplicationMapper orderRefundApplicationMapper, OrderMapper orderMapper, UserService userService, PaymentLogService paymentLogService, SalesUserService salesUserService) {
        this.orderRefundApplicationMapper = orderRefundApplicationMapper;
        this.orderMapper = orderMapper;
        this.userService = userService;
        this.paymentLogService = paymentLogService;
        this.salesUserService = salesUserService;
    }

    public Integer create(OrderRefundApplication orderRefundApplication){
        OrderDetail order = new OrderDetail();
        order.setOrderCode(orderRefundApplication.getOrderId());
        OrderDetail orderDetail = orderMapper.order(order);
        if (Objects.isNull(orderDetail)){
            OrderDetail order1 = new OrderDetail();
            order1.setId(orderDetail.getId());
            order1.setAfterStatus("yes");
           if (orderMapper.updateOrderById(order1)<=0){
               return -1;
           }
        }
        return this.orderRefundApplicationMapper.create(orderRefundApplication);
    }

    public Integer updateById(OrderRefundApplication orderRefundApplication){
       // if (Objects.equals(orderRefundApplication.getAfterStatus(),"yes")){
            OrderDetail order = new OrderDetail();
            order.setOrderCode(orderRefundApplication.getOrderId());
            order.setAfterStatus(orderRefundApplication.getAfterStatus());
            if (orderMapper.updateOrder(order)<=0){
                return -1;
            }
       // }
        return this.orderRefundApplicationMapper.updateById(orderRefundApplication);
    }

    public List<OrderRefundApplication> orderRefundApplicationList(OrderRefundApplication orderRefundApplication){
        return this.orderRefundApplicationMapper.orderRefundApplicationList(orderRefundApplication);
    }

    public List<OrderRefundApplication> list(OrderRefundApplication orderRefundApplication){
        return this.orderRefundApplicationMapper.list(orderRefundApplication);
    }

    /**
     * 后台管理系统--分页查询订单信息
     * @param param
     * @return
     */
    public PageQueryObject pageQuery(OrderGridParam param) throws InvocationTargetException, IntrospectionException, InstantiationException, IllegalAccessException {
        int count = orderRefundApplicationMapper.pageQueryCount(param);
        int page = param.getPage();
        int rows = param.getRows();
        //起始行
        param.setStart((page-1)*rows);
        //总记录数
        int totalPages = (count%rows==0?count/rows:count/rows+1);
        if(totalPages < page){
            page = 1;
            param.setPage(page);
            param.setStart(0);
        }
        List<OrderRefundPage> goods = orderRefundApplicationMapper.page(param);
        PageQueryObject result = new PageQueryObject();
        result.setRows(goods);
        result.setPage(page);
        result.setRecords(rows);
        result.setTotal(totalPages);
        return result;
    }


    /**
     * 后台管理--查询订单详情
     * @return
     */
    public OrderResult detail(Long id) {
        OrderResult order = new OrderResult();
        //账款订单详情信息
        //售后订单详细信息
        OrderRefundApplication orderRefund=new OrderRefundApplication();
        orderRefund.setId(id);
        OrderRefundApplication orderRefundApplication = orderRefundApplicationMapper.refundInfo(orderRefund);

        OrderResult order1 = orderRefundApplicationMapper.searchOrderById(orderRefundApplication.getOrderId());
        order1.setOrderRefundApplication(orderRefundApplication);

        if (Objects.nonNull(order1)) {
            //用户信息
            User user = userService.user(order1.getUserId());
            if (Objects.nonNull(user)) {
                order1.setShopName(user.getShopName());
                order1.setUserName(user.getUserName());
                order1.setPhone(user.getPhone());
                order1.setAddressDetail(user.getAddressDetail());
                order1.setDeliveryAddress(user.getAddressDetail());
            }
            //支付信息
            PaymentLog paymentLog = paymentLogService.getPaymentLog(order1.getOrderCode());
            if (Objects.nonNull(paymentLog)) {
                order1.setPaymentTime(paymentLog.getPaymentTime());
            }
            //业务员信息
            SalesUser salesUser = salesUserService.findById(order1.getSalesmanId());
            if (Objects.nonNull(salesUser)) {
                order1.setSalesmanName(salesUser.getSalesmanName());
            }
            //商品信息
            List<OrderGoods> orderGoods = orderMapper.searchOrderGoods(order1.getId());
            if (Objects.nonNull(orderGoods)) {
                order1.setOrderGoodsList(orderGoods);
            }
        }
        return order1;
    }

}
