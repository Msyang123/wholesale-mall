package com.lhiot.mall.wholesale.order.service;

import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.base.DataMergeUtils;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.order.domain.DebtOrder;
import com.lhiot.mall.wholesale.order.domain.DebtOrderResult;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.gridparam.DebtOrderGridParam;
import com.lhiot.mall.wholesale.order.mapper.DebtOrderMapper;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.pay.service.PaymentLogService;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@Transactional
/**
 * 欠款账单
 */
public class DebtOrderService {
    private final DebtOrderMapper debtOrderMapper;

    private final UserService userService;

    private final OrderService orderService;

    private final SnowflakeId snowflakeId;

    private final PaymentLogService paymentLogService;

    @Autowired
    public DebtOrderService(DebtOrderMapper debtOrderMapper, UserService userService, OrderService orderService, SnowflakeId snowflakeId, PaymentLogService paymentLogService) {
        this.debtOrderMapper = debtOrderMapper;
        this.userService = userService;
        this.orderService = orderService;
        this.snowflakeId=snowflakeId;
        this.paymentLogService = paymentLogService;
    }

    /**
     * 创建账款账款订单
     * @param debtOrder
     * @return
     */
    public int create(DebtOrder debtOrder){
        //产生欠款账款订单编码

        debtOrder.setCheckStatus("unpaid");//未支付
        debtOrder.setOrderDebtCode(snowflakeId.stringId());
        debtOrder.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return debtOrderMapper.save(debtOrder);
    }

    /**
     * 审核通过账款订单
     * @param debtOrder
     * @return
     */
    public int passAuditing(DebtOrder debtOrder){
        debtOrder.setCheckTime(new Timestamp(System.currentTimeMillis()));
        List<OrderDetail> orderDetailList=orderService.searchOrdersByOrderCodes(debtOrder.getOrderIds().split(","));
        for (OrderDetail item:orderDetailList){
            OrderDetail updateOrderDetial=new OrderDetail();
            updateOrderDetial.setOrderCode(item.getOrderCode());
            updateOrderDetial.setPayStatus("paid");//支付状态：paid-已支付 unpaid-未支付
            orderService.updateOrder(updateOrderDetial);

            PaymentLog paymentLog=new PaymentLog();
            //写入日志
            paymentLog.setPaymentType("offline");//balance-余额支付 wechat-微信 offline-线下支付
            paymentLog.setPaymentStep("paid");//sign-签名成功 paid-支付成功
            paymentLog.setOrderCode(item.getOrderCode());
            paymentLog.setOrderId(item.getId());
            paymentLog.setUserId(item.getUserId());
            paymentLog.setPaymentFrom("debt");//支付来源于 order-订单 debt-账款 invoice-发票 recharge-充值
            paymentLog.setTotalFee(item.getPayableFee()+item.getDeliveryFee());
            paymentLogService.insertPaymentLog(paymentLog);
        }
        return debtOrderMapper.updateDebtOrderByCode(debtOrder);
    }
    /**
     * 审核不通过账款订单
     * @param debtOrder
     * @return
     */
    public int unpassAuditing(DebtOrder debtOrder){
        debtOrder.setCheckTime(new Timestamp(System.currentTimeMillis()));
        return debtOrderMapper.updateDebtOrderByCode(debtOrder);
    }

    /**
     * 提交欠款账款订单审核
     * @param debtOrder
     * @return
     */
    public int updateDebtOrderByCode(DebtOrder debtOrder){
        return debtOrderMapper.updateDebtOrderByCode(debtOrder);
    }

    /**
     * 依据欠款账款订单编码查询欠款账款订单
     * @param debtOrderCode
     * @return
     */
    public DebtOrder findByCode(String debtOrderCode){
        return debtOrderMapper.findByCode(debtOrderCode);
    }

    /**
     * 依据订单号模糊查找账款订单
     * @param orderCode
     * @return
     */
    public DebtOrder findByOrderIdLike(String orderCode){
        return debtOrderMapper.findByOrderIdLike(orderCode);
    }


    /**
     * 后台管理系统--分页查询账款订单信息
     * @param param
     * @return
     */
    public PageQueryObject pageQuery(DebtOrderGridParam param) throws InvocationTargetException, IntrospectionException, InstantiationException, IllegalAccessException {
        String phone = param.getPhone();
        String shopName = param.getShopName();
        User userParam = new User();
        userParam.setPhone(phone);
        userParam.setShopName(shopName);
        List<DebtOrder> debtOrderList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        List<OrderDetail> orderDetailList = new ArrayList<>();
        List<PaymentLog> paymentLogList = new ArrayList<>();
        List<DebtOrderResult> debtOrderResults = new ArrayList<>();
        int count = 0;
        int page = param.getPage();
        int rows = param.getRows();
        //总记录数
        int totalPages = 0;
        if( (Objects.equals(phone,"") || Objects.isNull(phone) ) && (Objects.equals(shopName,"") || Objects.isNull(shopName))){//未传手机号查询条件,先根据条件查询分页的账款订单列表及用户ids，再根据ids查询用户信息列表
            count = debtOrderMapper.pageQueryCount(param);
            //起始行
            param.setStart((page-1)*rows);
            //总记录数
            totalPages = (count%rows==0?count/rows:count/rows+1);
            if(totalPages < page){
                page = 1;
                param.setPage(page);
                param.setStart(0);
            }
            debtOrderList = debtOrderMapper.pageQuery(param);
            List<Long> userIds = new ArrayList<>();
            List<Long> orderIds = new ArrayList<>();
            if(debtOrderList != null && debtOrderList.size() > 0){//查询账款订单对应的用户ID列表与账款订单ID列表
                for(DebtOrder debtOrder : debtOrderList){
                    long userId = debtOrder.getUserId();
                    long orderId = debtOrder.getId();
                    if(!userIds.contains(userId)){//用户id去重
                        userIds.add(userId);
                    }
                    if(!orderIds.contains(orderId)){
                        orderIds.add(orderId);
                    }
                }
                userList = userService.search(userIds);//根据用户ID列表查询用户信息
            }
        }else{//传了手机号查询条件，先根据条件查询用户列表及用户ids，再根据ids和账款订单其他信息查询账款订单信息列表
            userList = userService.searchByPhoneOrName(userParam);
            List<Long> userIds = new ArrayList<Long>();
            if(userList != null && userList.size() > 0){
                for(User user : userList){
                    userIds.add(user.getId());
                }
                param.setUserIds(userIds);
                count = debtOrderMapper.pageQueryCount(param);
                //起始行
                param.setStart((page-1)*rows);
                //总记录数
                totalPages = (count%rows==0?count/rows:count/rows+1);
                if(totalPages < page){
                    page = 1;
                    param.setPage(page);
                    param.setStart(0);
                }
                debtOrderList = debtOrderMapper.pageQuery(param);//根据用户ID列表及其他查询条件查询账款订单信息
                List<Long> orderIds = new ArrayList<Long>();
                if(debtOrderList != null && debtOrderList.size() > 0){
                    for(DebtOrder debtOrder : debtOrderList){
                        orderIds.add(debtOrder.getId());
                    }
                }
            }
        }
        PageQueryObject result = new PageQueryObject();
        if(debtOrderList != null && debtOrderList.size() > 0){//如果账款订单信息不为空,将账款订单列表与用户信息列表进行行数据组装
            debtOrderResults = DataMergeUtils.dataMerge(debtOrderList,userList,"userId","id",DebtOrderResult.class);
        }
        result.setPage(page);
        result.setRecords(rows);
        result.setTotal(totalPages);
        result.setRows(debtOrderResults);//将查询记录放入返回参数中
        return result;
    }

    /**
     * 后台管理系统--查询账款订单详情
     * @return
     */
    public DebtOrderResult detail(Long id) {
        //账款订单详情信息
        DebtOrderResult debtOrderResult = debtOrderMapper.searchDebtOrderById(id);
        if (Objects.nonNull(debtOrderResult)) {
            //用户信息
            User user1 = userService.user(debtOrderResult.getUserId());
            if (Objects.nonNull(user1)) {
                debtOrderResult.setShopName(user1.getShopName());
                debtOrderResult.setUserName(user1.getUserName());
                debtOrderResult.setPhone(user1.getPhone());
            }
            //订单信息
            OrderDetail orderDetail = orderService.countFee(debtOrderResult.getOrderIds());
            if (Objects.nonNull(orderDetail)) {
                debtOrderResult.setPayableFee(orderDetail.getPayableFee());
                debtOrderResult.setDeliveryFee(orderDetail.getDeliveryFee());
                debtOrderResult.setDiscountFee(orderDetail.getDiscountFee());
                debtOrderResult.setTotalFee(orderDetail.getTotalFee());
            }
           /* //支付信息
            PaymentLog paymentLog = paymentLogService.countFee(debtOrderResult.getOrderIds());
            if (Objects.nonNull(paymentLog)) {
                debtOrderResult.setPayTotalFee(paymentLog.getTotalFee());
            }*/
        }
        return debtOrderResult;
    }

    /**
     * 导出账款订单
     * @return
     */
    public List<Map<String, Object>> exportData(DebtOrderGridParam param){
        return debtOrderMapper.exportData(param);
    }

}
