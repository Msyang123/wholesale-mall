package com.lhiot.mall.wholesale.pay.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.microx.common.exception.ServiceException;
import com.lhiot.mall.wholesale.base.DateFormatUtil;
import com.lhiot.mall.wholesale.base.JacksonUtils;
import com.lhiot.mall.wholesale.base.StringReplaceUtil;
import com.lhiot.mall.wholesale.goods.domain.Goods;
import com.lhiot.mall.wholesale.goods.service.GoodsService;
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
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private final RabbitTemplate rabbit;

    @Autowired
    public PayService(PaymentLogService paymentLogService, UserService userService, OrderService orderService, DebtOrderService debtOrderService, InvoiceService invoiceService, Warehouse warehouse, GoodsService goodsService, RabbitTemplate rabbit){
        this.paymentLogService=paymentLogService;
        this.userService=userService;
        this.orderService=orderService;
        this.debtOrderService=debtOrderService;
        this.invoiceService=invoiceService;
        this.warehouse=warehouse;
        this.goodsService = goodsService;
        this.rabbit = rabbit;
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
     * 余额支付订单
     * @return
     */
    public int currencyPay(OrderDetail orderDetail) {
        User user= userService.user(orderDetail.getUserId());
        if(Objects.isNull(user)){
            throw new ServiceException("用户信息不存在");
        }
        //需要支付金额
        int needPayFee=orderDetail.getPayableFee()+orderDetail.getDeliveryFee();
        //扣除之后金额
        int afterPay=user.getBalance()-needPayFee;
        if(afterPay<0){
            throw new ServiceException("用户余额不足");
        }
        User updateUser=new User();
        updateUser.setId(orderDetail.getUserId());
        updateUser.setBalance(-needPayFee);//需要扣除的值
        boolean updateResult=userService.updateUser(updateUser);//扣除用户余额
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
        updateUser.setBalance(-needPayFee);//需要扣除的值
        boolean updateResult=userService.updateUser(updateUser);//扣除用户余额
        if(updateResult){
            debtOrder.setCheckStatus("unaudited");//设置审核中
            debtOrderService.updateDebtOrderByCode(debtOrder);

            PaymentLog paymentLog=new PaymentLog();
            //写入日志
            paymentLog.setPaymentType("balance");//支付类型：balance-余额支付 wechat-微信 offline-线下支付
            paymentLog.setPaymentStep("paid");//支付步骤：sign-签名成功 paid-支付成功
            paymentLog.setOrderCode(debtOrder.getOrderDebtCode());
            paymentLog.setOrderId(debtOrder.getId());
            paymentLog.setUserId(debtOrder.getUserId());
            paymentLog.setPaymentFrom("debt");//支付来源：order-订单 debt-账款 invoice-发票 recharge-充值
           /* paymentLog.setPaymentOrderType(0);
            paymentLog.setPaymentStep(3);//0签名 1余额支付 2账款订单未支付 3账款订单已支付 4支付回调 5充值回调 6欠款订单支付回调  7 发票支付回调
            paymentLog.setOrderCode(debtOrder.getOrderDebtCode());
            paymentLog.setOrderId(debtOrder.getId());
            paymentLog.setUserId(debtOrder.getUserId());
            paymentLog.setPaymentTime(new Timestamp(System.currentTimeMillis()));
            paymentLog.setPaymentFrom(0);//支付来源于 0订单 1发票
            paymentLog.setPaymentOrderType(1);//订单类型 0线上订单 1账款订单*/
            paymentLog.setTotalFee(needPayFee);
            paymentLogService.insertPaymentLog(paymentLog);
            return 1;
        }else{
            throw new ServiceException("扣除用户余额失败");
        }
    }

    /**
     * 减商品库存 发货 修改订单状态为待收货
     * @param orderDetail
     */
    public int sendToStock(OrderDetail orderDetail){
        orderDetail.getOrderGoodsList().forEach(item->{
            Goods goods=new Goods();
            goods.setId(item.getGoodsId());
            goods.setReduceStockLimit(item.getQuanity());//递减
            goodsService.update(goods);
        });
        //FIXME 发送订单到海鼎总仓
        Inventory inventory=new Inventory();
        inventory.setUuid(UUID.randomUUID().toString());
        inventory.setSenderCode("9646");
        inventory.setSenderWrh("07310101");
        inventory.setReceiverCode(null);
        inventory.setContactor("老曹");
        inventory.setPhoneNumber("18888888888");
        inventory.setDeliverAddress("五一大道98号");
        inventory.setRemark("快点送");
        inventory.setOcrDate(new Date());
        inventory.setFiller("填单人");
        inventory.setSeller("销售员");
        inventory.setSouceOrderCls("批发商城");
        inventory.setNegInvFlag("1");
        inventory.setMemberCode(null);
        inventory.setFreight(new BigDecimal(21.3));

        //清单
        List<Inventory.WholeSaleDtl> wholeSaleDtlList=new ArrayList<>();
        Inventory.WholeSaleDtl wholeSaleDtl1=inventory.new WholeSaleDtl();
        wholeSaleDtl1.setSkuId("010100100011");
        wholeSaleDtl1.setQty(new BigDecimal(3));
        wholeSaleDtl1.setPrice(new BigDecimal(100.1));
        wholeSaleDtl1.setTotal(null);
        wholeSaleDtl1.setFreight(null);
        wholeSaleDtl1.setPayAmount(new BigDecimal((99.1)));
        wholeSaleDtl1.setUnitPrice(null);
        wholeSaleDtl1.setPriceAmount(new BigDecimal(200.1));
        wholeSaleDtl1.setBuyAmount(new BigDecimal(99.2));
        wholeSaleDtl1.setBusinessDiscount(new BigDecimal(0.1));
        wholeSaleDtl1.setPlatformDiscount(new BigDecimal(0));
        wholeSaleDtl1.setQpc(new BigDecimal(5));
        wholeSaleDtl1.setQpcStr("1*5");

        wholeSaleDtlList.add(wholeSaleDtl1);
        inventory.setProducts(wholeSaleDtlList);

        List<Inventory.Pay> pays=new ArrayList<>();
        Inventory.Pay pay=inventory.new Pay();

        pay.setTotal(new BigDecimal(234.56));
        pay.setPayName("现金支付");
        pays.add(pay);
        inventory.setPays(pays);

        String hdCode=warehouse.savenew2state(inventory);
        log.info(hdCode);
        //获取海鼎总仓返回的订单号
        //TODO 修改订单并且发送海鼎总仓订单
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
        }
        return result;
    }
    /**
     * 余额支付发票
     * @return
     */
    public int currencyPay(Invoice invoice) {
        User user= userService.user(invoice.getUserId());
        if(Objects.isNull(user)){
            throw new ServiceException("用户信息不存在");
        }
        //需要支付金额
        int needPayFee=invoice.getTaxFee();
        //扣除之后金额
        int afterPay=user.getBalance()-needPayFee;
        if(afterPay<0){
            throw new ServiceException("用户余额不足");
        }
        User updateUser=new User();
        updateUser.setId(invoice.getUserId());
        updateUser.setBalance(-needPayFee);//需要扣除的值
        boolean updateResult=userService.updateUser(updateUser);//扣除用户余额
        if(updateResult){

            invoice.setInvoiceStatus("yes");//开票状态 0未开 1已付款 2已开
            invoiceService.updateInvoiceByCode(invoice);

            PaymentLog paymentLog=new PaymentLog();
            //写入日志
            paymentLog.setPaymentStep("paid");//支付步骤：sign-签名成功 paid-支付成功
            paymentLog.setOrderCode(invoice.getInvoiceCode());
            paymentLog.setOrderId(invoice.getId());
            paymentLog.setUserId(invoice.getUserId());
            paymentLog.setPaymentFrom("invoice");//支付来源：order-订单 debt-账款 invoice-发票 recharge-充值
            paymentLog.setPaymentType("balance");//支付类型：balance-余额支付 wechat-微信 offline-线下支付
           /* paymentLog.setPaymentOrderType(0);
            paymentLog.setPaymentStep(1);//0签名 1余额支付 2账款订单未支付 3账款订单已支付 4支付回调 5充值回调 6欠款订单支付回调  7 发票支付回调
            paymentLog.setOrderCode(invoice.getInvoiceCode());
            paymentLog.setOrderId(invoice.getId());
            paymentLog.setUserId(invoice.getUserId());
            paymentLog.setPaymentTime(new Timestamp(System.currentTimeMillis()));
            paymentLog.setPaymentFrom(1);//支付来源于 0订单 1发票
            paymentLog.setPaymentOrderType(2);//订单类型 0线上订单 1账款订单 2发票*/
            paymentLog.setTotalFee(needPayFee);
            paymentLogService.insertPaymentLog(paymentLog);
            return 1;
        }else{
            throw new ServiceException("扣除用户余额失败");
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

}
