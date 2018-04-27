package com.lhiot.mall.wholesale.pay.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhiot.mall.wholesale.base.DateFormatUtil;
import com.lhiot.mall.wholesale.user.wechat.WeChatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@Transactional
@Slf4j
public class PayService {

    private ObjectMapper om = new ObjectMapper();
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
        packageParams.put("out_trade_no", System.currentTimeMillis()+rechargeCode);// 商户订单号
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
        packageParams.put("out_trade_no", System.currentTimeMillis()+orderCode);// 商户订单号
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
}
