package com.lhiot.mall.wholesale.pay.service;

import com.fasterxml.jackson.databind.ObjectMapper;
<<<<<<< HEAD
import com.lhiot.mall.wholesale.base.DateFormatUtil;
=======
import com.leon.microx.common.exception.ServiceException;
import com.lhiot.mall.wholesale.order.domain.DebtOrder;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;
import com.lhiot.mall.wholesale.order.service.DebtOrderService;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.pay.mapper.PaymentLogMapper;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.service.UserService;
import com.lhiot.mall.wholesale.user.wechat.DateFormatUtil;
>>>>>>> sgsl/master
import com.lhiot.mall.wholesale.user.wechat.WeChatUtil;
import com.sgsl.hd.client.HaiDingClient;
import com.sgsl.hd.client.vo.OrderReduceData;
import com.sgsl.hd.client.vo.ProductsData;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
@Slf4j
public class PayService {

    private ObjectMapper om = new ObjectMapper();
    private final PaymentLogMapper paymentLogMapper;
    private final UserService userService;
    private final OrderService orderService;
    private final DebtOrderService debtOrderService;
    private final HaiDingClient hdClient;

    @Autowired
    public PayService(PaymentLogMapper paymentLogMapper,UserService userService,OrderService orderService,DebtOrderService debtOrderService,HaiDingClient hdClient){
        this.paymentLogMapper=paymentLogMapper;
        this.userService=userService;
        this.orderService=orderService;
        this.debtOrderService=debtOrderService;
        this.hdClient=hdClient;
    }
    /**
     * 微信充值支付签名
     * @return String
     */
    public String wxRechargePay(String ipAddress,String openId,int rechargeFee,String userAgent,String rechargeCode,WeChatUtil weChatUtil) throws Exception {
        Map<String, Object> ret = new HashMap<>();
        ret.put("state", "failure");
        if (StringUtils.isEmpty(openId)) {
            ret.put("msg", "用户ID为空！");
            return om.writeValueAsString(ret);
        }

        if (rechargeFee < 1) {
            ret.put("msg", "您输入的金额不正确！");
            return om.writeValueAsString(ret);
        }

        String currTime = DateFormatUtil.format3(new Date());
        String strTime = currTime.substring(8, currTime.length());
        String nonce = strTime + weChatUtil.buildRandom(4);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, weChatUtil.getProperties().getWeChatPay().getTimeoutExpress());// 设置6分钟过期
        String timeExpire = DateFormatUtil.format3(cal.getTime());
        log.info("=================开始微信签名===============");
        SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
        packageParams.put("appid", weChatUtil.getProperties().getWeChatOauth().getAppId());
        packageParams.put("mch_id", weChatUtil.getProperties().getWeChatPay().getPartnerId());
        packageParams.put("nonce_str", nonce);// 随机串
        packageParams.put("body", "水果熟了 - 批发商城用户充值");// 商品描述
        packageParams.put("attach", "yy");// 附加数据
        packageParams.put("out_trade_no", rechargeCode);// 商户订单号
        packageParams.put("total_fee", rechargeFee);// 微信支付金额单位为（分）
        packageParams.put("time_expire", timeExpire);
        packageParams.put("spbill_create_ip", ipAddress);// 订单生成的机器ip
        // IP
        packageParams.put("notify_url", weChatUtil.getProperties().getWeChatPay().getRechargeNotifyUrl());// 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
        packageParams.put("trade_type", "JSAPI");
        packageParams.put("openid", openId);
        String sign = weChatUtil.createSign(weChatUtil.getProperties().getWeChatPay().getPartnerKey(), packageParams); // 获取签名
        packageParams.put("sign", sign);
        log.info("=================获取预支付ID===============");
        String xml = weChatUtil.getRequestXml(packageParams); // 获取请求微信的XML
        String prepayId = weChatUtil.sendWeChatGetPrepayId(xml);
        if (StringUtils.isEmpty(prepayId)) {
            throw new RuntimeException("微信预支付出错");
        }
        log.info("=================微信预支付成功，响应到JSAPI完成微信支付===============");
        SortedMap<Object, Object> finalpackage = new TreeMap<Object, Object>();
        finalpackage.put("appId", weChatUtil.getProperties().getWeChatOauth().getAppId());
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        finalpackage.put("timeStamp", timestamp);
        finalpackage.put("nonceStr", nonce);
        String packages = "prepay_id=" + prepayId;
        finalpackage.put("package", packages);
        finalpackage.put("signType", "MD5");
        String finalsign = weChatUtil.createSign(weChatUtil.getProperties().getWeChatPay().getPartnerKey(), finalpackage);

        Map<String,String> rechargeSisgn = new HashMap();
        rechargeSisgn.put("appid", weChatUtil.getProperties().getWeChatOauth().getAppId());
        rechargeSisgn.put("timeStamp", timestamp);
        rechargeSisgn.put("nonceStr", nonce);
        rechargeSisgn.put("packageValue", packages);
        rechargeSisgn.put("sign", finalsign);
        rechargeSisgn.put("orderId", rechargeCode);
        char agent = userAgent.charAt(userAgent.indexOf("MicroMessager") + 15);
        rechargeSisgn.put("agent", new String(new char[] { agent }));
        return om.writeValueAsString(rechargeSisgn);
    }

    /**
     * 微信订单支付
     * @return String
     */
    public String wxOrderPay(String ipAddress,String openId,int orderFee,String userAgent,String orderCode,WeChatUtil weChatUtil) throws Exception {
        Map<String, Object> ret = new HashMap<>();
        // 微信支付
        log.info("=================账户余额不足, 开始微信预支付===============");
        String currTime = DateFormatUtil.format3(new Date());
        String strTime = currTime.substring(8, currTime.length());
        String nonce = strTime + weChatUtil.buildRandom(4);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, weChatUtil.getProperties().getWeChatPay().getTimeoutExpress());// 注意：最短失效时间间隔必须大于5分钟
        String timeExpire = DateFormatUtil.format3(cal.getTime());
        log.info("=================开始微信签名===============");

        SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
        packageParams.put("appid", weChatUtil.getProperties().getWeChatOauth().getAppId());
        packageParams.put("mch_id", weChatUtil.getProperties().getWeChatPay().getPartnerId());
        packageParams.put("nonce_str", nonce);// 随机串
        packageParams.put("body", "水果熟了 - 批发商城订单支付");// 描述
        packageParams.put("attach", "jj");// 附加数据
        packageParams.put("out_trade_no", orderCode);// 商户订单号
        packageParams.put("total_fee", orderFee);// 微信支付金额单位为（分）
        packageParams.put("time_start", strTime);// 订单生成时间
        packageParams.put("time_expire", timeExpire);// 订单失效时间
        packageParams.put("spbill_create_ip", ipAddress);// 订单生成的机器
        // IP

        packageParams.put("notify_url",weChatUtil.getProperties().getWeChatPay().getOrderNotifyUrl());// 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
        packageParams.put("trade_type", "JSAPI");
        packageParams.put("openid", openId);
        String sign = weChatUtil.createSign(weChatUtil.getProperties().getWeChatPay().getPartnerKey(), packageParams); // 获取签名
        packageParams.put("sign", sign);
        String xml = weChatUtil.getRequestXml(packageParams); // 获取请求微信的XML
        log.info("=================获取预支付ID===============" + xml);
        String prepayId = weChatUtil.sendWeChatGetPrepayId(xml);
        if (StringUtils.isEmpty(prepayId)) {
            ret.put("msg", "微信预支付出错（无法获取预支付编号）");
            ret.put("resouce_from", 0);
            return om.writeValueAsString(ret);
        }
        log.info("=================微信预支付成功，生成JSAPI签名===============");
        SortedMap<Object, Object> finalpackage = new TreeMap<Object, Object>();
        finalpackage.put("appId", weChatUtil.getProperties().getWeChatOauth().getAppId());
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        finalpackage.put("timeStamp", timestamp);
        finalpackage.put("nonceStr", nonce);
        String packages = "prepay_id=" + prepayId;
        finalpackage.put("package", packages);
        finalpackage.put("signType", "MD5");
        String finalsign = weChatUtil.createSign(weChatUtil.getProperties().getWeChatPay().getPartnerKey(), finalpackage);

        // JSAPI弹出用户充值成功后的提示页面，用户点击右上角"完成"按钮，JSAPI通知微信支付成功，可以回调了
        log.info("=================响应到JSAPI，完成微信支付===============");
        Map<String, Object> orderSisgn = new HashMap<String, Object>();
        orderSisgn.put("appid", weChatUtil.getProperties().getWeChatOauth().getAppId());
        orderSisgn.put("timeStamp", timestamp);
        orderSisgn.put("nonceStr", nonce);
        orderSisgn.put("packageValue", packages);
        orderSisgn.put("sign", finalsign);
        orderSisgn.put("orderId", orderCode);
        char agent = userAgent.charAt(userAgent.indexOf("MicroMessager") + 15);
        orderSisgn.put("agent", new String(new char[] { agent }));
        return om.writeValueAsString(orderSisgn);
    }

    /**
     * 微信开票支付签名
     * @return String
     */
    public String wxInvoicePay(String ipAddress,String openId,int invoiceFee,String userAgent,String invoiceCode,WeChatUtil weChatUtil) throws Exception {
        Map<String, Object> ret = new HashMap<>();
        ret.put("state", "failure");
        if (StringUtils.isEmpty(openId)) {
            ret.put("msg", "用户ID为空！");
            return om.writeValueAsString(ret);
        }

        if (invoiceFee < 1) {
            ret.put("msg", "您输入的金额不正确！");
            return om.writeValueAsString(ret);
        }

        String currTime = DateFormatUtil.format3(new Date());
        String strTime = currTime.substring(8, currTime.length());
        String nonce = strTime + weChatUtil.buildRandom(4);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, weChatUtil.getProperties().getWeChatPay().getTimeoutExpress());// 设置6分钟过期
        String timeExpire = DateFormatUtil.format3(cal.getTime());
        log.info("=================开始微信签名===============");
        SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
        packageParams.put("appid", weChatUtil.getProperties().getWeChatOauth().getAppId());
        packageParams.put("mch_id", weChatUtil.getProperties().getWeChatPay().getPartnerId());
        packageParams.put("nonce_str", nonce);// 随机串
        packageParams.put("body", "水果熟了 - 批发商城用户开票");// 商品描述
        packageParams.put("attach", "zz");// 附加数据
        packageParams.put("out_trade_no", invoiceCode);// 商户订单号
        packageParams.put("total_fee", invoiceFee);// 微信支付金额单位为（分）
        packageParams.put("time_expire", timeExpire);
        packageParams.put("spbill_create_ip", ipAddress);// 订单生成的机器ip
        // IP
        packageParams.put("notify_url", weChatUtil.getProperties().getWeChatPay().getRechargeNotifyUrl());// 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
        packageParams.put("trade_type", "JSAPI");
        packageParams.put("openid", openId);
        String sign = weChatUtil.createSign(weChatUtil.getProperties().getWeChatPay().getPartnerKey(), packageParams); // 获取签名
        packageParams.put("sign", sign);
        log.info("=================获取预支付ID===============");
        String xml = weChatUtil.getRequestXml(packageParams); // 获取请求微信的XML
        String prepayId = weChatUtil.sendWeChatGetPrepayId(xml);
        if (StringUtils.isEmpty(prepayId)) {
            throw new RuntimeException("微信预支付出错");
        }
        log.info("=================微信预支付成功，响应到JSAPI完成微信支付===============");
        SortedMap<Object, Object> finalpackage = new TreeMap<Object, Object>();
        finalpackage.put("appId", weChatUtil.getProperties().getWeChatOauth().getAppId());
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        finalpackage.put("timeStamp", timestamp);
        finalpackage.put("nonceStr", nonce);
        String packages = "prepay_id=" + prepayId;
        finalpackage.put("package", packages);
        finalpackage.put("signType", "MD5");
        String finalsign = weChatUtil.createSign(weChatUtil.getProperties().getWeChatPay().getPartnerKey(), finalpackage);

        Map<String,String> rechargeSisgn = new HashMap();
        rechargeSisgn.put("appid", weChatUtil.getProperties().getWeChatOauth().getAppId());
        rechargeSisgn.put("timeStamp", timestamp);
        rechargeSisgn.put("nonceStr", nonce);
        rechargeSisgn.put("packageValue", packages);
        rechargeSisgn.put("sign", finalsign);
        rechargeSisgn.put("orderId", invoiceCode);
        char agent = userAgent.charAt(userAgent.indexOf("MicroMessager") + 15);
        rechargeSisgn.put("agent", new String(new char[] { agent }));
        return om.writeValueAsString(rechargeSisgn);
    }

    /**
     * 微信账款订单支付签名
     * @return String
     */
    public String wxDebtopderPay(String ipAddress,String openId,int invoiceFee,String userAgent,String invoiceCode,WeChatUtil weChatUtil) throws Exception {
        Map<String, Object> ret = new HashMap<>();
        ret.put("state", "failure");
        if (StringUtils.isEmpty(openId)) {
            ret.put("msg", "用户ID为空！");
            return om.writeValueAsString(ret);
        }

        if (invoiceFee < 1) {
            ret.put("msg", "您输入的金额不正确！");
            return om.writeValueAsString(ret);
        }

        String currTime = DateFormatUtil.format3(new Date());
        String strTime = currTime.substring(8, currTime.length());
        String nonce = strTime + weChatUtil.buildRandom(4);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, weChatUtil.getProperties().getWeChatPay().getTimeoutExpress());// 设置6分钟过期
        String timeExpire = DateFormatUtil.format3(cal.getTime());
        log.info("=================开始微信签名===============");
        SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
        packageParams.put("appid", weChatUtil.getProperties().getWeChatOauth().getAppId());
        packageParams.put("mch_id", weChatUtil.getProperties().getWeChatPay().getPartnerId());
        packageParams.put("nonce_str", nonce);// 随机串
        packageParams.put("body", "水果熟了 - 批发商城用户开票");// 商品描述
        packageParams.put("attach", "zz");// 附加数据
        packageParams.put("out_trade_no", invoiceCode);// 商户订单号
        packageParams.put("total_fee", invoiceFee);// 微信支付金额单位为（分）
        packageParams.put("time_expire", timeExpire);
        packageParams.put("spbill_create_ip", ipAddress);// 订单生成的机器ip
        // IP
        packageParams.put("notify_url", weChatUtil.getProperties().getWeChatPay().getRechargeNotifyUrl());// 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
        packageParams.put("trade_type", "JSAPI");
        packageParams.put("openid", openId);
        String sign = weChatUtil.createSign(weChatUtil.getProperties().getWeChatPay().getPartnerKey(), packageParams); // 获取签名
        packageParams.put("sign", sign);
        log.info("=================获取预支付ID===============");
        String xml = weChatUtil.getRequestXml(packageParams); // 获取请求微信的XML
        String prepayId = weChatUtil.sendWeChatGetPrepayId(xml);
        if (StringUtils.isEmpty(prepayId)) {
            throw new RuntimeException("微信预支付出错");
        }
        log.info("=================微信预支付成功，响应到JSAPI完成微信支付===============");
        SortedMap<Object, Object> finalpackage = new TreeMap<Object, Object>();
        finalpackage.put("appId", weChatUtil.getProperties().getWeChatOauth().getAppId());
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        finalpackage.put("timeStamp", timestamp);
        finalpackage.put("nonceStr", nonce);
        String packages = "prepay_id=" + prepayId;
        finalpackage.put("package", packages);
        finalpackage.put("signType", "MD5");
        String finalsign = weChatUtil.createSign(weChatUtil.getProperties().getWeChatPay().getPartnerKey(), finalpackage);

        Map<String,String> rechargeSisgn = new HashMap();
        rechargeSisgn.put("appid", weChatUtil.getProperties().getWeChatOauth().getAppId());
        rechargeSisgn.put("timeStamp", timestamp);
        rechargeSisgn.put("nonceStr", nonce);
        rechargeSisgn.put("packageValue", packages);
        rechargeSisgn.put("sign", finalsign);
        rechargeSisgn.put("orderId", invoiceCode);
        char agent = userAgent.charAt(userAgent.indexOf("MicroMessager") + 15);
        rechargeSisgn.put("agent", new String(new char[] { agent }));
        return om.writeValueAsString(rechargeSisgn);
    }

    /**
     * 余额支付订单
     * @return
     */
    public int currencyPay(OrderDetail orderDetail) {
        User user= userService.user(orderDetail.getUserId());
        if(Objects.isNull(user)){
            throw new ServiceException("用户信息不存在");
        }
        //需要支付金额
        int needPayFee=orderDetail.getOrderNeedFee()+orderDetail.getDeliveryFee();
        //扣除之后金额
        int afterPay=user.getBalance()-needPayFee;
        if(afterPay<0){
            throw new ServiceException("用户余额不足");
        }
        User updateUser=new User();
        updateUser.setId(orderDetail.getUserId());
        updateUser.setBalance(needPayFee);//需要扣除的值
        boolean updateResult=userService.updateUser(updateUser);//扣除用户余额
        if(updateResult){
            //发送海鼎订单信息 测试环境发送到左家塘店
            //需要配置海鼎相关配置到yml中
            OrderReduceData reduceData = null;
            try {
                reduceData = hdReduce(orderDetail,user);
            } catch (Exception e) {
                throw new ServiceException("订单转换门店发送数据失败");
            }
            if(reduceData!=null&&!hdClient.orderReduce(reduceData)){
                //fixme 海鼎没有发送成功 重试处理
            }

            orderService.updateOrderStatus(orderDetail);

            PaymentLog paymentLog=new PaymentLog();
            //写入日志
            paymentLog.setPaymentOrderType(0);
            paymentLog.setPaymentStep(2);//余额支付
            paymentLog.setOrderCode(orderDetail.getOrderCode());
            paymentLog.setOrderId(orderDetail.getId());
            paymentLog.setUserId(orderDetail.getUserId());
            paymentLog.setPaymentTime(new Timestamp(System.currentTimeMillis()));
            paymentLog.setPaymentFrom(0);//支付来源于 0订单 1发票
            paymentLog.setTotalFee(needPayFee);
            paymentLogMapper.insertPaymentLog(paymentLog);
            //修改订单并且发送海鼎订单
            orderDetail.setOrderStatus(3);//已付款状态
            orderDetail.setCurrentOrderStaus(1);//待付款状态

            //修改订单状态为已支付状态
            return 1;
        }else{
            throw new ServiceException("扣除用户余额失败");
        }
    }

    /**
     * 余额支付账款订单
     * @return
     */
    public int currencyPay(DebtOrder debtOrder) {
        User user= userService.user(debtOrder.getUserId());
        if(Objects.isNull(user)){
            throw new ServiceException("用户信息不存在");
        }
        //需要支付金额
        int needPayFee=debtOrder.getDebtFee();
        //扣除之后金额
        int afterPay=user.getBalance()-needPayFee;
        if(afterPay<0){
            throw new ServiceException("用户余额不足");
        }
        User updateUser=new User();
        updateUser.setId(debtOrder.getUserId());
        updateUser.setBalance(needPayFee);//需要扣除的值
        boolean updateResult=userService.updateUser(updateUser);//扣除用户余额
        if(updateResult){
            debtOrder.setCheckStatus(1);//设置审核中
            debtOrderService.updateDebtOrderByCode(debtOrder);

            PaymentLog paymentLog=new PaymentLog();
            //写入日志
            paymentLog.setPaymentOrderType(0);
            paymentLog.setPaymentStep(2);//余额支付
            paymentLog.setOrderCode(debtOrder.getOrderDebtCode());
            paymentLog.setOrderId(debtOrder.getId());
            paymentLog.setUserId(debtOrder.getUserId());
            paymentLog.setPaymentTime(new Timestamp(System.currentTimeMillis()));
            paymentLog.setPaymentFrom(0);//支付来源于 0订单 1发票
            paymentLog.setPaymentStep(4);//支付步骤 0签名 1支付回调 2余额支付 3货到付款未支付 4货到付款已支付 5充值签名 6充值回调
            paymentLog.setPaymentOrderType(1);//订单类型 0线上订单 1账款订单
            paymentLog.setTotalFee(needPayFee);
            paymentLogMapper.insertPaymentLog(paymentLog);
            return 1;
        }else{
            throw new ServiceException("扣除用户余额失败");
        }
    }

    /**
     * 海鼎减库存
     * @param orderDetail
     * @param user
     * @return
     * @throws Exception
     */
    public OrderReduceData hdReduce(OrderDetail orderDetail,User user) throws Exception {

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        OrderReduceData returnData=new OrderReduceData();
        returnData.setOrderId(orderDetail.getOrderCode());
        returnData.setOrderUserId(orderDetail.getUserId());

        returnData.setNeedPay(orderDetail.getOrderNeedFee());

        returnData.setPayTime(sdf.format(new Date()));
        returnData.setDeliveryType("1");
        returnData.setOrderType("1");
        returnData.setTotal(orderDetail.getTotal());
        returnData.setDiscount(orderDetail.getOrderDiscountFee());

        JSONObject json = new JSONObject(orderDetail.getDeliveryAddress());
        returnData.setReceiverName(json.getString("deliveryName"));
        returnData.setReceiverMobile(json.getString("deliveryMobile"));

        returnData.setAddressDetail(json.getString("deliveryDetail"));

        returnData.setDeliveryTime(sdf.format(orderDetail.getDeliveryTime()));

        //需要去除掉特殊的表情符号
        String replacedUsername=replaceEmoji(replaceByte4(user.getUserName()));
        returnData.setNickname(replacedUsername);
        returnData.setPhoneNum(user.getPhone());
        //设置订单门店 测试环境设置的只有一个
        //TODO 正式环境需要提供门店编码替换
        returnData.setStoreId("07310106");
        returnData.setStoreName("水果熟了-左家塘店");
        //备注
        returnData.setNRemark(replaceEmoji(replaceByte4(orderDetail.getRemarks()))+"配送时间："+returnData.getDeliveryTime());
        //设置订单商品
        for(OrderGoods item:orderDetail.getOrderGoodsList()){
            ProductsData productsData=new ProductsData();
            productsData.setProductCode(item.getHdSkuId());
            productsData.setProductName(item.getGoodsName());
            //FIXME 待验证 可能可以不传递
            //XXX 待验证amount 和productAmount 是否正确
            productsData.setAmount((double)item.getQuanity());//数量
            productsData.setProductAmount(item.getStandardWeight().doubleValue());//重量
            productsData.setPrice(item.getGoodsPrice());//价格
            productsData.setDeductibleFee(item.getDiscountGoodsPrice());
            returnData.getProductsData().add(productsData);
        }
        return returnData;
    }

    public static String replaceByte4(String str) {
        if (str==null||"".equals(str.trim())) {
            return "";
        }
        try {
            byte[] conbyte = str.getBytes();
            for (int i = 0; i < conbyte.length; i++) {
                if ((conbyte[i] & 0xF8) == 0xF0) {// 如果是4字节字符
                    for (int j = 0; j < 4; j++) {
                        conbyte[i + j] = 0x30;// 将当前字符变为“0000”
                    }
                    i += 3;
                }
            }
            str = new String(conbyte);
            return str.replaceAll("0000", "");
        } catch (Throwable e) {
            return "";
        }
    }
    public static String replaceEmoji(String str) {
        if (str==null||"".equals(str.trim())) {
            return "";
        }
        try {
            Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                    Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
            Matcher emojiMatcher = emoji.matcher(str);
            if (emojiMatcher.find()) {
                String temp = str.substring(emojiMatcher.start(), emojiMatcher.end());
                str = str.replaceAll(temp, "");
            }
            return str;
        } catch (Throwable e) {
            return "";
        }
    }
}
