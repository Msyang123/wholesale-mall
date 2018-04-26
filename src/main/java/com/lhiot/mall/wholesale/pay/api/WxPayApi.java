package com.lhiot.mall.wholesale.pay.api;

import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.pay.service.PayService;
import com.lhiot.mall.wholesale.user.wechat.PaymentProperties;
import com.lhiot.mall.wholesale.user.wechat.WeChatUtil;
import com.lhiot.mall.wholesale.user.wechat.XPathParser;
import com.lhiot.mall.wholesale.user.wechat.XPathWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Api(description = "微信支付接口")
@RestController
@RequestMapping("/wechat")
public class WxPayApi {
	
	private final PayService payService;

    private final OrderService orderService;
	private final WeChatUtil weChatUtil;

    private final SnowflakeId snowflakeId;



	@Autowired
	public WxPayApi(PayService payService,OrderService orderService, PaymentProperties properties,SnowflakeId snowflakeId){

        this.payService = payService;
        this.orderService=orderService;
        this.weChatUtil = new WeChatUtil(properties);
        this.snowflakeId=snowflakeId;
	}
	
    @GetMapping("/orderpay/sign")
    @ApiOperation(value = "微信订单支付签名", response = String.class)
    public ResponseEntity<String> orderpaySign(HttpServletRequest request,@RequestParam("openId") String openId,@RequestParam("orderCode") String orderCode) throws Exception {
        OrderDetail orderDetail= orderService.searchOrder(orderCode);
	    //通过加密后的openIdAfterDm5查找数据库openId
        String wxOrderSignStr=payService.wxOrderPay(getRemoteAddr(request),openId,
                orderDetail.getOrderNeedFee()+orderDetail.getDeliveryFee(),getUserAgent(request),orderCode,weChatUtil);
        //FIXME 写订单签名日志
    	return ResponseEntity.ok(wxOrderSignStr);
    }

    @GetMapping("/rechargepay/sign")
    @ApiOperation(value = "微信充值支付签名", response = String.class)
    public ResponseEntity<String> rechargepaySign(HttpServletRequest request,@RequestParam("openId") String openId,@RequestParam("rechargeFee") int rechargeFee) throws Exception {

        String wxRechargeSignStr=payService.wxRechargePay(getRemoteAddr(request),openId,rechargeFee,getUserAgent(request),snowflakeId.stringId(),weChatUtil);
        //FIXME 写充值签名日志
        return ResponseEntity.ok(wxRechargeSignStr);
    }

    @PostMapping("/order/notify")
    @ApiOperation(value = "微信订单支付回调", response = String.class)
    public ResponseEntity<String> orderNotify(HttpServletRequest request) throws Exception {
        log.info("========支付成功，后台回调=======");
        XPathParser xpath = weChatUtil.getParametersByWeChatCallback(request);
        XPathWrapper wrap = new XPathWrapper(xpath);
        String resultCode = wrap.get("result_code");
        if ("SUCCESS".equalsIgnoreCase(resultCode)) {
            String userId = wrap.get("attach");
            String totalFee = wrap.get("total_fee");
            int fee = Integer.parseInt(totalFee);
            //获取传达的附加参数获取用户信息
           log.info("userId:"+userId+"fee:"+fee);
           boolean myDoIsOk=true;//我们处理的业务
            if (myDoIsOk) {
               //广播订单支付成功true, "success"
                ResponseEntity.ok("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                                + "<xml><return_code><![CDATA[SUCCESS]]></return_code>"
                                + "<return_msg><![CDATA[OK]]></return_msg></xml>");
            }
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/recharge/notify")
    @ApiOperation(value = "微信充值支付回调", response = String.class)
    public ResponseEntity<String> rechargeNotify(HttpServletRequest request) throws Exception {
        log.info("========支付成功，后台回调=======");
        XPathParser xpath = weChatUtil.getParametersByWeChatCallback(request);
        XPathWrapper wrap = new XPathWrapper(xpath);
        String resultCode = wrap.get("result_code");
        if ("SUCCESS".equalsIgnoreCase(resultCode)) {
            String userId = wrap.get("attach");
            String totalFee = wrap.get("total_fee");
            int fee = Integer.parseInt(totalFee);
            //获取传达的附加参数获取用户信息
            log.info("userId:"+userId+"fee:"+fee);
            boolean myDoIsOk=true;//我们处理的业务
            if (myDoIsOk) {
                //广播订单支付成功true, "success"
                ResponseEntity.ok("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<xml><return_code><![CDATA[SUCCESS]]></return_code>"
                        + "<return_msg><![CDATA[OK]]></return_msg></xml>");
            }
        }
        return ResponseEntity.ok().build();
    }

    private String getUserAgent(HttpServletRequest request){
        return request.getHeader("user-agent");
    }
    private   String getRemoteAddr(HttpServletRequest request) {
		if ("nginx".equals(weChatUtil.getProperties().getWeChatPay().getProxy())) {
			return request.getHeader("X-Real-IP");
		}
		return request.getRemoteAddr();
	}
}
