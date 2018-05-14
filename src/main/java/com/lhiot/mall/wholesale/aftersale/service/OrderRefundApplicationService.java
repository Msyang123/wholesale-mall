package com.lhiot.mall.wholesale.aftersale.service;

import com.lhiot.mall.wholesale.aftersale.domain.OrderRefundApplication;
import com.lhiot.mall.wholesale.aftersale.domain.OrderResult;
import com.lhiot.mall.wholesale.aftersale.mapper.OrderRefundApplicationMapper;
import com.lhiot.mall.wholesale.base.PageQueryObject;
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
        return this.orderRefundApplicationMapper.create(orderRefundApplication);
    }

    public Integer updateById(OrderRefundApplication orderRefundApplication){
        return this.orderRefundApplicationMapper.updateById(orderRefundApplication);
    }

    public List<OrderRefundApplication> orderRefundApplicationList(Long userId){
        return this.orderRefundApplicationMapper.orderRefundApplicationList(userId);
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
        String phone = param.getPhone();
        User userParam = new User();
        userParam.setPhone(phone);
        List<OrderGridResult> orderGridResultList = new ArrayList<OrderGridResult>();
        List<User> userList = new ArrayList<User>();
        List<PaymentLog> paymentLogList = new ArrayList<PaymentLog>();
        int count = 0;
        int page = param.getPage();
        int rows = param.getRows();
        //总记录数
        int totalPages = 0;
        userList = userService.searchByPhoneOrName(userParam);
        List<Long> userIds = new ArrayList<Long>();
        if(userList != null && userList.size() > 0){
            for(User user : userList){
                userIds.add(user.getId());
            }
            param.setUserIds(userIds);
            count = orderMapper.pageQueryCount(param);
            //起始行
            param.setStart((page-1)*rows);
            //总记录数
            totalPages = (count%rows==0?count/rows:count/rows+1);
            if(totalPages < page){
                page = 1;
                param.setPage(page);
                param.setStart(0);
            }
            orderGridResultList = orderMapper.pageQuery(param);//根据用户ID列表及其他查询条件查询用户信息
            List<Long> orderIds = new ArrayList<Long>();
            if(orderGridResultList != null && orderGridResultList.size() > 0){
                for(OrderGridResult orderGridResult : orderGridResultList){
                    orderIds.add(orderGridResult.getId());
                }
            }
            paymentLogList = paymentLogService.getPaymentLogList(orderIds);//根据订单ID列表查询支付信息
        }
        PageQueryObject result = new PageQueryObject();
        if(orderGridResultList != null && orderGridResultList.size() > 0){//如果订单信息不为空,将订单列表与用户信息列表进行行数据组装
            //根据用户id与订单中的用户id匹配
            for (OrderGridResult orderGridResult : orderGridResultList) {
                Long orderUserId = orderGridResult.getUserId();
                for (User user : userList) {
                    Long uId = user.getId();
                    if (Objects.equals(orderUserId, uId)) {
                        orderGridResult.setPhone(user.getPhone());
                        orderGridResult.setShopName(user.getShopName());
                        orderGridResult.setUserName(user.getUserName());
                        break;
                    }
                }
            }
            //根据订单id和支付记录orderId进行信息匹配
            for (OrderGridResult orderGridResult : orderGridResultList) {
                Long orderId = orderGridResult.getId();
                for (PaymentLog paymentLog : paymentLogList) {
                    Long pOrderId = paymentLog.getOrderId();
                    if (Objects.equals(orderId, pOrderId)) {
                        orderGridResult.setPaymentTime(paymentLog.getPaymentTime());
                        break;
                    }
                }
            }
        }

        //售后订单状态字段数据组装
        for (OrderGridResult orderGridResult : orderGridResultList) {
            for (Map item:param.getAuditStatuss()) {
                if (Objects.equals(item.get("orderCode"),orderGridResult.getOrderCode())){
                    orderGridResult.setAuditStatus(item.get("status").toString());
                    break;
                }
            }
        }

        result.setPage(page);
        result.setRecords(rows);
        result.setTotal(totalPages);
        result.setRows(orderGridResultList);//将查询记录放入返回参数中
        return result;
    }


    /**
     * 后台管理--查询订单详情
     * @return
     */
    public OrderResult detail(Long id) {
        //账款订单详情信息
        OrderResult order = orderRefundApplicationMapper.searchOrderById(id);
        if (Objects.nonNull(order)){
            //售后订单详细信息
            OrderRefundApplication orderRefund=new OrderRefundApplication();
            orderRefund.setOrderId(order.getOrderCode());
            OrderRefundApplication orderRefundApplication = orderRefundApplicationMapper.refundInfo(orderRefund);
            order.setOrderRefundApplication(orderRefundApplication);
        }
        if (Objects.nonNull(order)) {
            //用户信息
            User user = userService.user(order.getUserId());
            if (Objects.nonNull(user)) {
                order.setShopName(user.getShopName());
                order.setUserName(user.getUserName());
                order.setPhone(user.getPhone());
                order.setAddressDetail(user.getAddressDetail());
                order.setDeliveryAddress(user.getAddressDetail());
            }
            //支付信息
            PaymentLog paymentLog = paymentLogService.getPaymentLog(order.getOrderCode());
            if (Objects.nonNull(paymentLog)) {
                order.setPaymentTime(paymentLog.getPaymentTime());
            }
            //业务员信息
            SalesUser salesUser = salesUserService.findById(order.getSalesmanId());
            if (Objects.nonNull(salesUser)) {
                order.setSalesmanName(salesUser.getSalesmanName());
            }
            //商品信息
            List<OrderGoods> orderGoods = orderMapper.searchOrderGoods(order.getId());
            if (Objects.nonNull(orderGoods)) {
                order.setOrderGoodsList(orderGoods);
            }
        }
        return order;
    }

}
