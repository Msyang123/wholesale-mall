package com.lhiot.mall.wholesale.pay.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.microx.common.exception.ServiceException;
import com.leon.microx.util.Calculator;
import com.lhiot.mall.wholesale.ApplicationConfiguration;
import com.lhiot.mall.wholesale.base.CalculateUtil;
import com.lhiot.mall.wholesale.base.DateFormatUtil;
import com.lhiot.mall.wholesale.base.JacksonUtils;
import com.lhiot.mall.wholesale.base.StringReplaceUtil;
import com.lhiot.mall.wholesale.goods.domain.Goods;
import com.lhiot.mall.wholesale.goods.domain.GoodsStandard;
import com.lhiot.mall.wholesale.goods.service.GoodsService;
import com.lhiot.mall.wholesale.goods.service.GoodsStandardService;
import com.lhiot.mall.wholesale.invoice.domain.Invoice;
import com.lhiot.mall.wholesale.invoice.service.InvoiceService;
import com.lhiot.mall.wholesale.order.domain.DebtOrder;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;
import com.lhiot.mall.wholesale.order.service.DebtOrderService;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.pay.hdsend.Inventory;
import com.lhiot.mall.wholesale.pay.hdsend.Warehouse;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.service.UserService;
import com.lhiot.mall.wholesale.user.wechat.WeChatUtil;
import com.sgsl.auditing.MD5;
import com.sgsl.hd.client.vo.OrderReduceData;
import com.sgsl.hd.client.vo.ProductsData;

import com.sgsl.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Objects;

@Service
@Transactional
@Slf4j
public class PayService {

    private ObjectMapper om = new ObjectMapper();
    private final PaymentLogService paymentLogService;
    private final UserService userService;
    private final OrderService orderService;
    private final DebtOrderService debtOrderService;
    private final InvoiceService invoiceService;
    private final Warehouse warehouse;
    private final GoodsService goodsService;
    private final GoodsStandardService goodsStandardService;
    private final RabbitTemplate rabbit;
    private final ApplicationConfiguration config;
    @Autowired
    public PayService(PaymentLogService paymentLogService, 
    		UserService userService, OrderService orderService, 
    		DebtOrderService debtOrderService, InvoiceService invoiceService, 
    		Warehouse warehouse, GoodsService goodsService, 
    		GoodsStandardService goodsStandardService, RabbitTemplate rabbit,
    		ApplicationConfiguration config){
        this.paymentLogService=paymentLogService;
        this.userService=userService;
        this.orderService=orderService;
        this.debtOrderService=debtOrderService;
        this.invoiceService=invoiceService;
        this.warehouse=warehouse;
        this.goodsService = goodsService;
        this.goodsStandardService = goodsStandardService;
        this.rabbit = rabbit;
        this.config = config;
    }
    /**
     * 微信充值支付签名
     * @return String
     */
    public String wxRechargePay(String ipAddress,String openId,int rechargeFee,String userAgent,String rechargeCode,WeChatUtil weChatUtil) throws Exception {
        Map<String, Object> ret = new HashMap<>();
        ret.put("state", "failure");
        if (StringUtils.isEmpty(openId)) {
            ret.put("msg", "用户信息为空！");
            return om.writeValueAsString(ret);
        }
        User user= userService.searchUserByOpenid(openId);
        if(Objects.isNull(user)){
            ret.put("msg", "未找到用户信息！");
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
        packageParams.put("attach", user.getId());// 附加数据 用户id
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
    public String wxInvoicePay(String ipAddress,String openId,int invoiceFee,String userAgent,String invoiceCode,String attach,WeChatUtil weChatUtil) throws Exception {
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
        packageParams.put("attach", attach);// 附加数据
        packageParams.put("out_trade_no", invoiceCode);// 商户订单号
        packageParams.put("total_fee", invoiceFee);// 微信支付金额单位为（分）
        packageParams.put("time_expire", timeExpire);
        packageParams.put("spbill_create_ip", ipAddress);// 订单生成的机器ip
        // IP
        packageParams.put("notify_url", weChatUtil.getProperties().getWeChatPay().getInvoiceNotifyUrl());// 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
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
        packageParams.put("notify_url", weChatUtil.getProperties().getWeChatPay().getOrderOfflineNotifyUrl());// 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
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
     * 微信补差额支付签名
     * @param ipAddress
     * @param openId
     * @param supplement 需要补的差额
     * @param userAgent
     * @param payCode
     * @param orderCode 补差额的当前订单
     * @param weChatUtil
     * @return
     * @throws Exception
     */
    public String wxSupplementPay(String ipAddress,String openId,int supplement,String userAgent,
    		String payCode,String orderCode,WeChatUtil weChatUtil) throws Exception {
        Map<String, Object> ret = new HashMap<>();
        ret.put("state", "failure");
        if (StringUtils.isEmpty(openId)) {
            ret.put("msg", "用户信息为空！");
            return om.writeValueAsString(ret);
        }
        User user= userService.searchUserByOpenid(openId);
        if(Objects.isNull(user)){
            ret.put("msg", "未找到用户信息！");
            return om.writeValueAsString(ret);
        }

        if (supplement < 1) {
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
        packageParams.put("body", "水果熟了 - 订单"+orderCode+"补差额");// 商品描述
        packageParams.put("attach", user.getId()+","+orderCode);// 附加数据 用户id和订单号
        packageParams.put("out_trade_no", payCode);// 商户订单号
        packageParams.put("total_fee", supplement);// 微信支付金额单位为（分）
        packageParams.put("time_expire", timeExpire);
        packageParams.put("spbill_create_ip", ipAddress);// 订单生成的机器ip
        // IP
        packageParams.put("notify_url", weChatUtil.getProperties().getWeChatPay().getSupplementNotifyUrl());// 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
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

        Map<String,String> rechargeSisgn = new HashMap<>();
        rechargeSisgn.put("appid", weChatUtil.getProperties().getWeChatOauth().getAppId());
        rechargeSisgn.put("timeStamp", timestamp);
        rechargeSisgn.put("nonceStr", nonce);
        rechargeSisgn.put("packageValue", packages);
        rechargeSisgn.put("sign", finalsign);
        rechargeSisgn.put("orderId", payCode);
        char agent = userAgent.charAt(userAgent.indexOf("MicroMessager") + 15);
        rechargeSisgn.put("agent", new String(new char[] { agent }));
        return om.writeValueAsString(rechargeSisgn);
    }
    
    /**
     * 余额支付订单
     * @return
     */
    public String currencyPay(OrderDetail orderDetail) {
        User user= userService.user(orderDetail.getUserId());
        if(Objects.isNull(user)){
            return "用户信息不存在";
        }
        //需要支付金额
        int needPayFee=orderDetail.getPayableFee()+orderDetail.getDeliveryFee();
        //扣除之后金额
        int afterPay=user.getBalance()-needPayFee;
        if(afterPay<0){
            return "用户余额不足";
        }
        User updateUser=new User();
        updateUser.setId(orderDetail.getUserId());
        updateUser.setBalance(-needPayFee);//需要扣除的值
        boolean updateResult=userService.updateBalance(updateUser);//扣除用户余额
        if(updateResult){
            //减商品库存
            orderDetail.setPayStatus("paid");//已支付
            sendToStock(orderDetail);

            PaymentLog paymentLog=new PaymentLog();
            //写入日志
            paymentLog.setPaymentType("balance");//balance-余额支付 wechat-微信 offline-线下支付
            paymentLog.setPaymentStep("paid");//sign-签名成功 paid-支付成功
            paymentLog.setOrderCode(orderDetail.getOrderCode());
            paymentLog.setOrderId(orderDetail.getId());
            paymentLog.setUserId(orderDetail.getUserId());
            paymentLog.setPaymentFrom("order");//支付来源于 order-订单 debt-账款 invoice-发票 recharge-充值
            paymentLog.setTotalFee(needPayFee);
            paymentLogService.insertPaymentLog(paymentLog);
            return null;
        }else{
            return "扣除用户余额失败";
        }
    }

    /**
     * 余额支付账款订单
     * @return
     */
    public String currencyPay(DebtOrder debtOrder) {
        User user= userService.user(debtOrder.getUserId());
        if(Objects.isNull(user)){
            return "用户信息不存在";
        }
        //需要支付金额
        int needPayFee=debtOrder.getDebtFee();
        //扣除之后金额
        int afterPay=user.getBalance()-needPayFee;
        if(afterPay<0){
            return "用户余额不足";
        }
        User updateUser=new User();
        updateUser.setId(debtOrder.getUserId());
        updateUser.setBalance(-needPayFee);//需要扣除的值
        boolean updateResult=userService.updateBalance(updateUser);//扣除用户余额
        if(updateResult){
            //修改账款订单为已支付
            DebtOrder saveDebtOrder=new DebtOrder();
            saveDebtOrder.setOrderDebtCode(debtOrder.getOrderDebtCode());
            saveDebtOrder.setCheckStatus("paid");
            saveDebtOrder.setPaymentType("balance");//支付类型：balance-余额支付 wechat-微信 offline-线下支付
            debtOrderService.updateDebtOrderByCode(saveDebtOrder);
            //修改订单为已支付状态
            List<OrderDetail> orderDetailList=orderService.searchOrdersByOrderCodes(debtOrder.getOrderIds().split(","));
            if(orderDetailList==null||orderDetailList.isEmpty()){
                return "未查找到相关订单";
            }
            for (OrderDetail item:orderDetailList){
                OrderDetail updateOrderDetail =new OrderDetail();
                updateOrderDetail.setPayStatus("paid");//支付状态：paid-已支付 unpaid-未支付
                updateOrderDetail.setOrderCode(item.getOrderCode());
                orderService.updateOrder(updateOrderDetail);

                OrderDetail searchOrder= orderService.searchOrder(item.getOrderCode());

                PaymentLog paymentLog=new PaymentLog();
                //写入日志
                paymentLog.setPaymentType("balance");//支付类型：balance-余额支付 wechat-微信 offline-线下支付
                paymentLog.setPaymentStep("paid");//支付步骤：sign-签名成功 paid-支付成功
                paymentLog.setOrderCode(searchOrder.getOrderCode());
                paymentLog.setOrderId(searchOrder.getId());
                paymentLog.setUserId(searchOrder.getUserId());
                paymentLog.setPaymentFrom("debt");//支付来源：order-订单 debt-账款 invoice-发票 recharge-充值
                paymentLog.setTotalFee(searchOrder.getPayableFee()+searchOrder.getDeliveryFee());
                paymentLogService.insertPaymentLog(paymentLog);
            }

            return null;
        }else{
            return "扣除用户余额失败";
        }
    }

    /**
     * 微信支付账款订单
     * @return
     */
    public String weixinPayDebt(DebtOrder debtOrder,String bankType,String transactionId,Integer totalFee) {
        User user= userService.user(debtOrder.getUserId());
        if(Objects.isNull(user)){
            return "用户信息不存在";
        }
        //修改账款订单为已支付
        DebtOrder saveDebtOrder=new DebtOrder();
        saveDebtOrder.setOrderDebtCode(debtOrder.getOrderDebtCode());
        saveDebtOrder.setCheckStatus("paid");
        saveDebtOrder.setPaymentType("wechat");//支付类型：balance-余额支付 wechat-微信 offline-线下支付
        debtOrderService.updateDebtOrderByCode(saveDebtOrder);
        //修改订单为已支付状态
        List<OrderDetail> orderDetailList=orderService.searchOrdersByOrderCodes(debtOrder.getOrderIds().split(","));
        if(orderDetailList==null||orderDetailList.isEmpty()){
            return "未查找到相关订单";
        }
        for (OrderDetail item:orderDetailList){
            OrderDetail updateOrderDetail =new OrderDetail();
            updateOrderDetail.setPayStatus("paid");//支付状态：paid-已支付 unpaid-未支付
            updateOrderDetail.setOrderCode(item.getOrderCode());
            orderService.updateOrder(updateOrderDetail);

            PaymentLog paymentLog =paymentLogService.getPaymentLog(item.getOrderCode());
            paymentLog.setPaymentStep("paid");//支付步骤：sign-签名成功 paid-支付成功
            paymentLog.setBankType(bankType);//银行类型
            paymentLog.setTransactionId(transactionId);//微信流水
            paymentLog.setTotalFee(totalFee);//支付金额
            paymentLog.setPaymentTime(new Timestamp(System.currentTimeMillis()));//支付时间
            paymentLogService.updatePaymentLog(paymentLog);
        }


        return null;
    }

    /**
     * 减商品库存 发货 修改订单状态为待收货
     * @param orderDetail
     */
    public int sendToStock(OrderDetail orderDetail){
        //{"id":7,"sex":"male","phone":"18207493756","address":"麓谷企业广场F3栋","city":"湖南省/长沙市/岳麓区","isDefault":"yes","userId":1,"name":"gfhfg"}
        Inventory inventory=new Inventory();
        inventory.setUuid(UUID.randomUUID().toString());
        inventory.setSenderCode("9646");
        inventory.setSenderWrh("07310101");
        inventory.setReceiverCode(config.getReceiverCode());
        String deliveryAddress=orderDetail.getDeliveryAddress();
        try {
            Map<String,Object> addressInfo= JacksonUtils.fromJson(deliveryAddress,Map.class);
            String contactor = this.obtainContactor(String.valueOf(addressInfo.get("name")), orderDetail);
            inventory.setContactor(contactor);
            inventory.setPhoneNumber(String.valueOf(addressInfo.get("phone")));
            inventory.setDeliverAddress(String.valueOf(addressInfo.get("city"))+String.valueOf(addressInfo.get("address")));
        } catch (IOException e) {
            throw new ServiceException("减商品库存 发货转换发货地址错误"+e.getLocalizedMessage());
        }

        inventory.setRemark(orderDetail.getRemarks());
        inventory.setOcrDate(new Date());
        inventory.setFiller("批发填单人");
        inventory.setSeller("批发销售员");
        inventory.setSouceOrderCls("批发商城");
        inventory.setNegInvFlag("1");
        inventory.setFreight(new BigDecimal(Calculator.div(orderDetail.getDeliveryFee(),100.0,2)));
        inventory.setMemberCode(null);

        //清单
        List<Inventory.WholeSaleDtl> wholeSaleDtlList=new ArrayList<>();

        orderDetail.getOrderGoodsList().forEach(item->{
            Goods goods=new Goods();
            goods.setId(item.getGoodsId());
            goods.setReduceStockLimit(item.getQuanity());//递减
            goodsService.update(goods);

            GoodsStandard goodsStandard= goodsStandardService.goodsStandard(item.getGoodsStandardId());
            Inventory.WholeSaleDtl wholeSaleDtl=inventory.new WholeSaleDtl();
            wholeSaleDtl.setSkuId(goodsStandard.getBarCode());
            wholeSaleDtl.setQty(item.getStandardWeight().multiply(new BigDecimal(item.getQuanity())));//数量乘以重量
            wholeSaleDtl.setPrice(new BigDecimal(item.getGoodsPrice()/100.0));
            wholeSaleDtl.setTotal(new BigDecimal(item.getGoodsPrice()/100.0).multiply(new BigDecimal(item.getQuanity())));
            wholeSaleDtl.setFreight(null);
            wholeSaleDtl.setPayAmount(new BigDecimal(item.getDiscountGoodsPrice()/100.0));
            wholeSaleDtl.setUnitPrice(new BigDecimal(item.getGoodsPrice()/100.0));
            wholeSaleDtl.setPriceAmount(new BigDecimal(item.getGoodsPrice()/100.0));
            wholeSaleDtl.setBuyAmount(new BigDecimal(item.getGoodsPrice()/100.0));
            wholeSaleDtl.setBusinessDiscount(new BigDecimal((item.getGoodsPrice()-item.getDiscountGoodsPrice())/100.0));
            wholeSaleDtl.setPlatformDiscount(new BigDecimal(0));
            wholeSaleDtl.setQpc(item.getStandardWeight());
            wholeSaleDtl.setNote(goodsStandard.getGoodsName());
//            wholeSaleDtl.setQpcStr(goodsStandard.getGoodsName());

            wholeSaleDtlList.add(wholeSaleDtl);
        });

        inventory.setFreight(new BigDecimal(orderDetail.getDeliveryFee()/100.0));
        inventory.setProducts(wholeSaleDtlList);

        List<Inventory.Pay> pays=new ArrayList<>();
        Inventory.Pay pay=inventory.new Pay();

        pay.setTotal(new BigDecimal((orderDetail.getPayableFee()+orderDetail.getDeliveryFee())/100.0));
        pay.setPayName(orderDetail.getSettlementType());
        pays.add(pay);
        inventory.setPays(pays);

        String hdCode=warehouse.savenew2state(inventory);
        log.info(hdCode);
        //获取海鼎总仓返回的订单号
        //修改订单状态为已支付状态
        orderDetail.setHdCode(hdCode);//总仓编码
        orderDetail.setHdStatus("success");//海鼎发送成功
        orderDetail.setOrderStatus("undelivery");//待发货状态
        int result=orderService.updateOrder(orderDetail);
        //发布广播消息
        try {
            rabbit.convertAndSend("order-paid-event","", JacksonUtils.toJson(orderDetail));
        } catch (JsonProcessingException e) {
            log.error("支付订单发布广播消息"+e.getLocalizedMessage());
            throw new ServiceException("支付订单发布广播消息"+e.getLocalizedMessage());
        }
        return result;
    }
    /**
     * 余额支付发票
     * @return
     */
    public String currencyPay(Invoice invoice) {
        User user= userService.user(invoice.getUserId());
        if(Objects.isNull(user)){
            return "用户信息不存在";
        }
        //需要支付金额
        int needPayFee=invoice.getTaxFee();
        //扣除之后金额
        int afterPay=user.getBalance()-needPayFee;
        if(afterPay<0){
            return "用户余额不足";
        }
        if(needPayFee<=0){
            return "支付金额不能小于0";
        }
        User updateUser=new User();
        updateUser.setId(invoice.getUserId());
        updateUser.setBalance(-needPayFee);//需要扣除的值
        boolean updateResult=userService.updateBalance(updateUser);//扣除用户余额
        if(updateResult){
            //保存开票信息
            invoiceService.applyInvoice(invoice,"balance",null,null);
            return null;
        }else{
            return "扣除用户余额失败";
        }
    }

    /**
     * 微信回调计算签名
     * @param parameters
     * @param partnerKey
     * @return
     */
    public String createSign(final SortedMap<Object, Object> parameters, String partnerKey) {
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<Object, Object>> es = parameters.entrySet();
        for (Map.Entry<Object, Object> entry : es) {
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k).append("=").append(v).append("&");
            }
        }
        sb.append("key=").append(partnerKey);
        String sign = MD5.md5Hex(sb.toString());
        return sign.toUpperCase();
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

        returnData.setNeedPay(orderDetail.getPayableFee());

        returnData.setPayTime(sdf.format(new Date()));
        returnData.setDeliveryType("1");
        returnData.setOrderType("1");
        returnData.setTotal(orderDetail.getTotalFee());
        returnData.setDiscount(orderDetail.getDiscountFee());

        JSONObject json = new JSONObject(orderDetail.getDeliveryAddress());
        returnData.setReceiverName(json.getString("deliveryName"));
        returnData.setReceiverMobile(json.getString("deliveryMobile"));

        returnData.setAddressDetail(json.getString("deliveryDetail"));

        returnData.setDeliveryTime(sdf.format(orderDetail.getDeliveryTime()));

        //需要去除掉特殊的表情符号
        String replacedUsername=StringReplaceUtil.replaceEmoji(StringReplaceUtil.replaceByte4(user.getUserName()));
        returnData.setNickname(replacedUsername);
        returnData.setPhoneNum(user.getPhone());
        //设置订单门店 测试环境设置的只有一个
        //正式环境需要提供门店编码替换
        returnData.setStoreId("07310106");
        returnData.setStoreName("水果熟了-左家塘店");
        //备注
        returnData.setNRemark(StringReplaceUtil.replaceEmoji(StringReplaceUtil.replaceByte4(orderDetail.getRemarks()))+"配送时间："+returnData.getDeliveryTime());
        //设置订单商品
        for(OrderGoods item:orderDetail.getOrderGoodsList()){
            ProductsData productsData=new ProductsData();
            productsData.setProductCode(item.getHdSkuId());
            productsData.setProductName(item.getGoodsName());
            //待验证 可能可以不传递
            //XXX 待验证amount 和productAmount 是否正确
            productsData.setAmount((double)item.getQuanity());//数量
            productsData.setProductAmount(item.getStandardWeight().doubleValue());//重量
            productsData.setPrice(item.getGoodsPrice());//价格
            productsData.setDeductibleFee(item.getDiscountGoodsPrice());
            returnData.getProductsData().add(productsData);
        }
        return returnData;
    }

    /**
     * 获取收货人信息
     * @param userName 收货人名称
     * @param orderDetail 订单详情
     * @return
     */
    public String obtainContactor(String userName,OrderDetail orderDetail){
    	String hundred = "100";
    	String discountFee = CalculateUtil.division(orderDetail.getDiscountFee(), hundred, 2);
    	String deliveryFee = CalculateUtil.division(orderDetail.getDeliveryFee(), hundred, 2);
    	return userName+"|"+deliveryFee+"|"+discountFee;
    }
    
}
