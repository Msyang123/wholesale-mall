package com.lhiot.mall.wholesale.aftersale.service;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leon.microx.util.ImmutableMap;
import com.leon.microx.util.StringUtils;
import com.lhiot.mall.wholesale.aftersale.domain.ApplicationType;
import com.lhiot.mall.wholesale.aftersale.domain.OrderRefundApplication;
import com.lhiot.mall.wholesale.aftersale.domain.OrderRefundPage;
import com.lhiot.mall.wholesale.aftersale.domain.OrderRefundResult;
import com.lhiot.mall.wholesale.aftersale.domain.OrderResult;
import com.lhiot.mall.wholesale.aftersale.domain.PaymentType;
import com.lhiot.mall.wholesale.aftersale.domain.SupplementRecords;
import com.lhiot.mall.wholesale.aftersale.mapper.OrderRefundApplicationMapper;
import com.lhiot.mall.wholesale.base.PageQueryObject;
import com.lhiot.mall.wholesale.order.domain.DebtOrderResult;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;
import com.lhiot.mall.wholesale.order.domain.gridparam.OrderGridParam;
import com.lhiot.mall.wholesale.order.mapper.DebtOrderMapper;
import com.lhiot.mall.wholesale.order.mapper.OrderMapper;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.pay.domain.RefundLog;
import com.lhiot.mall.wholesale.pay.mapper.RefundLogMapper;
import com.lhiot.mall.wholesale.pay.service.PaymentLogService;
import com.lhiot.mall.wholesale.setting.domain.ParamConfig;
import com.lhiot.mall.wholesale.setting.mapper.SettingMapper;
import com.lhiot.mall.wholesale.user.domain.SalesUser;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.service.SalesUserService;
import com.lhiot.mall.wholesale.user.service.UserService;
import com.lhiot.mall.wholesale.user.wechat.PaymentProperties;
import com.lhiot.mall.wholesale.user.wechat.WeChatUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class OrderRefundApplicationService {

    private final OrderRefundApplicationMapper orderRefundApplicationMapper;

    private final OrderMapper orderMapper;

    private final UserService userService;
    private final SettingMapper settingMapper;
    
    private final PaymentLogService paymentLogService;

    private final RefundLogMapper refundLogMapper;
    private final SalesUserService salesUserService;
    private final WeChatUtil weChatUtil;
    private final DebtOrderMapper debtOrderMapper;

    @Autowired
    public OrderRefundApplicationService(OrderRefundApplicationMapper orderRefundApplicationMapper, 
    		OrderMapper orderMapper, UserService userService, PaymentLogService paymentLogService, 
    		SalesUserService salesUserService,
    		PaymentProperties paymentProperties,
    		RefundLogMapper refundLogMapper,
    		SettingMapper settingMapper,
    		DebtOrderMapper debtOrderMapper) {
        this.orderRefundApplicationMapper = orderRefundApplicationMapper;
        this.orderMapper = orderMapper;
        this.userService = userService;
        this.paymentLogService = paymentLogService;
        this.salesUserService = salesUserService;
        this.refundLogMapper = refundLogMapper;
        this.settingMapper = settingMapper;
        this.debtOrderMapper = debtOrderMapper;
        this.weChatUtil = new WeChatUtil(paymentProperties);
    }

    /**
     * 新增售后记录
     * @param orderRefundApplication
     * @return
     */
    public Integer create(OrderRefundApplication orderRefundApplication){
    	String applicationType = orderRefundApplication.getApplicationType();
    	if(ApplicationType.refund.toString().equals(applicationType) || 
    			ApplicationType.consult.toString().equals(applicationType)){
            OrderDetail order = new OrderDetail();
            order.setOrderCode(orderRefundApplication.getOrderId());
            order.setAfterStatus("yes");
            order.setCheckStatus("auditeding");
            //修改订单的售后状态
            if (orderMapper.updateOrder(order)<=0){
               return -1;
            }
    	}else if(ApplicationType.supplement.equals(applicationType)){
    		orderRefundApplication.setAuditStatus("agree");
    	}
        return this.orderRefundApplicationMapper.create(orderRefundApplication);
    }

    /**
     * 审核售后订单
     * @param orderRefundApplication
     * @return
     */
    public Integer updateById(OrderRefundApplication orderRefundApplication){
    	String auditStatus = orderRefundApplication.getAuditStatus();
    	String orderCode = orderRefundApplication.getOrderId();
    	OrderDetail orderDetail = orderMapper.searchOrder(orderCode);
    	if(Objects.isNull(orderDetail)){
    		return -1;
    	}
    	if("agree".equals(auditStatus)){
        	if("yes".equals(orderDetail.getAfterStatus())){
        		return -1;
        	}
        	//判断用户的实际支付金额和退款金额
        	int refundFee = this.vaildOrder(orderDetail, orderRefundApplication);
        	if(refundFee < 0){
        		return -1;
        	}
        	//调用退款接口
        	this.aftersaleRefund(orderDetail, refundFee);
    	}
    	//修改订单的售后状态
        OrderDetail param = new OrderDetail();
        param.setOrderCode(orderRefundApplication.getOrderId());
        param.setAfterStatus(orderRefundApplication.getAfterStatus());
        param.setCheckStatus(auditStatus);
        //修改订单的售后状态
        if (orderMapper.updateOrder(param)<=0){
            return -1;
        }
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
        //设置默认排序,创建时间倒序
        if(StringUtils.isBlank(param.getSidx())){
        	param.setSidx("a.audit_status desc,a.create_at");
        	param.setSord("desc");
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

    /**
     * 部分退款验证订单信息
     * @param orderDetail
     * @return
     */
    public int vaildOrder(OrderDetail orderDetail,OrderRefundApplication refundApplication){
    	int fee = -1;
    	//获取退款申请中的金额,实际退换给用户的金额
    	int returnFee = refundApplication.getOrderDiscountFee() - refundApplication.getDeliveryFee();
    	//获取订单的实付金额
    	Integer payableFee = orderDetail.getPayableFee();
    	Integer deliveryFee = Objects.isNull(orderDetail.getDeliveryFee()) ? 0:orderDetail.getDeliveryFee();
    	//用户购买时，实际花费的金额
    	Integer actualFee = payableFee + deliveryFee;
    	if(returnFee < actualFee){
    		fee = returnFee;
    	}
    	return fee;
    }
    

    /**
     * 售后退款
     * @param orderDetail 订单详情
     * @param refundFee 退款金额
     * @return
     */
    public String aftersaleRefund(OrderDetail orderDetail,Integer refundFee){
    	PaymentLog paymentLog = paymentLogService.getPaymentLog(orderDetail.getOrderCode());
    	if(Objects.isNull(refundFee)){
    		return "退款金额错误";
    	}
    	String orderCode = orderDetail.getOrderCode();
        //写入退款记录  t_whs_refund_log
        RefundLog refundLog = new RefundLog();
        refundLog.setPaymentLogId(paymentLog.getId());
        refundLog.setRefundFee(refundFee);
        refundLog.setRefundReason("售后退款");
        refundLog.setRefundTime(new Timestamp(System.currentTimeMillis()));
        refundLog.setTransactionId(paymentLog.getTransactionId());
        refundLog.setUserId(orderDetail.getUserId());
        
        //修改支付日志
        PaymentLog updatePaymentLog = new PaymentLog();
        updatePaymentLog.setOrderCode(orderDetail.getOrderCode());
        updatePaymentLog.setRefundFee(refundFee);
        
        //构建修改用户余额的参数
        User updateUser = new User();
        updateUser.setId(orderDetail.getUserId());
        updateUser.setBalance(refundFee);
        
        String refundType = "balanceRefund";
        switch (orderDetail.getSettlementType()) {
        //货到付款
        case "offline":
        	// 查询查询账款订单信息
        	DebtOrderResult debtOrder = debtOrderMapper.findByOrderCode(orderCode);
        	if(Objects.isNull(debtOrder)){
        		return "没有相应订单，请联系客服人员";
        	}
        	String paymentType = debtOrder.getPaymentType();//支付类型
        	//非微信支付(余额/现金/银行卡等支付方式)--返回余额
        	if(!PaymentType.wechat.toString().equals(paymentType)){
        		userService.updateBalance(updateUser);
        	}else {
        		//微信支付--原路返回
        		String debtOrderCode = debtOrder.getOrderDebtCode();//账款订单编号
                //退款 如果微信支付就微信退款
                String refundChatFee = weChatUtil.refund(debtOrderCode,debtOrder.getDebtFee(), refundFee);
                //检查为没有失败信息
                if (StringUtils.isNotBlank(refundChatFee)&&refundChatFee.indexOf("FAIL")==-1) {
                	refundType = "wechatRefund";
                } else {
                    return "微信退款失败，请联系客服";
                }
        	}
        	break;
        //微信支付
        case "wechat":
            //退款 如果微信支付就微信退款
            String refundChatFee = weChatUtil.refund(paymentLog.getOrderCode(),paymentLog.getTotalFee(), refundFee);
            //检查为没有失败信息
            if (StringUtils.isNotBlank(refundChatFee)&&refundChatFee.indexOf("FAIL")==-1) {
            	refundType = "wechatRefund";
            } else {
                return "微信退款失败，请联系客服";
            }
            break;
        //余额支付
        case "balance":
            userService.updateBalance(updateUser);
            break;
        default:
            log.info("退款未找到类型");
            break;
        }
	    //写退款日志和修改支付记录
        refundLog.setRefundType(refundType);
	    refundLogMapper.insertRefundLog(refundLog);
	    paymentLogService.updatePaymentLog(updatePaymentLog);
    	return null;
    }
    
    /**
     * 判断售后申请是否在售后时间范围内
     * @param timestamp
     * @return
     */
    public boolean withinTheTime(Timestamp timestamp){
    	boolean success = false;
    	ParamConfig config = settingMapper.searchConfigParam("afterSalePeriod");
    	String deadLine = config.getConfigParamValue();
    	if(StringUtils.isBlank(deadLine) || Objects.isNull(timestamp)){
    		return success;
    	}
    	//售后期限
    	int day = Integer.valueOf(deadLine.trim());
    	//计算售后期限
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss");
    	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String t = format.format(timestamp);
		LocalDateTime deadLineDay = LocalDateTime.parse(t,formatter).plusDays(day);
		
		//分当前时间比较
		LocalDateTime curTime = LocalDateTime.now();
		if(curTime.isBefore(deadLineDay)){
			success = true;
		}
    	return success;
    }
    
    /**
     * 判断当前订单是已经售后(只能是审核不通过的)
     * @param orderCode
     * @return
     */
    public boolean hasApply(String orderCode){
    	List<OrderRefundApplication> orderRefundApplications = orderRefundApplicationMapper.select(orderCode);
    	if(!orderRefundApplications.isEmpty()){
    		return true;
    	}
    	return false;
    }
    
    /**
     * 查询各种类型的售后
     * @param orderCode 订单编号
     * @param applicationType 申请售后类型
     * @return
     */
    public List<SupplementRecords> supplements(String orderCode,ApplicationType applicationType){
    	Map<String,Object> map = ImmutableMap.of("orderCode", orderCode, 
    			"applicationType", applicationType.toString());
    	return orderRefundApplicationMapper.supplements(map);
    }

    /**
     * 售后请求详情
     * @param orderCode
     * @param userId
     * @return
     */
    public OrderRefundResult orderRefundResult(String orderCode,Long userId){
    	Map<String,Object> param = ImmutableMap.of("orderCode", orderCode, "userId", userId);
    	OrderRefundResult orderRefundResult = orderRefundApplicationMapper.orderRefundResult(param);
    	if(Objects.isNull(orderRefundResult)){
    		return new OrderRefundResult();
    	}
    	//查询申请差额记录
    	List<SupplementRecords> sr = this.supplements(orderCode, ApplicationType.supplement);
    	if(Objects.nonNull(sr)){
    		orderRefundResult.setSupplements(sr);
    	}
    	//获取订单的实付金额
    	orderRefundResult.setPayableFee(0);
    	orderRefundResult.setOrderDeliveryFee(0);
    	OrderDetail orderDetail = orderMapper.searchOrder(orderCode);
    	if(Objects.nonNull(orderDetail)){
    		orderRefundResult.setPayableFee(orderDetail.getPayableFee());
    		orderRefundResult.setOrderDeliveryFee(orderDetail.getDeliveryFee());
    	}
    	return orderRefundResult;
    }
    
    /**
     * 判断是否可以补差额
     * @param orderCode 订单编号
     * @param receiveTime 售后时间
     * @return
     */
    public String trySupplement(String orderCode,Timestamp receiveTime){
    	String result = null;
    	if(StringUtils.isBlank(orderCode)){
    		result = "订单编号错误";
    		return result;
    	}
    	//售后期限判断
    	if(!this.withinTheTime(receiveTime)){
    		result = "超出售后期限，请联系客服";
    		return result;
    	}
    	//判断是否已经申请售后(未审批或者审批通过的)
    	List<OrderRefundApplication> ora = orderRefundApplicationMapper.select(orderCode);
    	if(Objects.nonNull(ora) && !ora.isEmpty()){
    		result = "存在售后申请，请联系客服";
    		return result;
    	}
    	return result;
    }
}
