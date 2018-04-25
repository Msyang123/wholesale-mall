package com.lhiot.mall.wholesale.pay.api;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Api(description = "支付接口")
@RestController
@RequestMapping("/wechat")
public class PayApi {
	
	private final PayService payService;

    private final WeChatUtil weChatUtil;

	@Autowired
	public PayApi(PayService payService, PaymentProperties properties){

        this.payService = payService;
        this.weChatUtil = new WeChatUtil(properties);
	}
	
    @GetMapping("/orderpay/sign")
    @ApiOperation(value = "微信订单支付签名", response = String.class)
    public ResponseEntity<String> orderpaySign(HttpServletRequest request) throws Exception {
	    String openIdAfterDm5=request.getParameter("openIdAfterDm5");
	    String orderFee=request.getParameter("orderFee");
	    int orderFeeIntVal=Integer.valueOf(orderFee);
	    //通过加密后的openIdAfterDm5查找数据库openId
        String openId="ouxJf0RQY0eCN6OWotvzwulqSFJQ";
        String orderCode="3333333"+System.currentTimeMillis();
        String wxOrderSignStr=payService.wxOrderPay(getRemoteAddr(request),openId,orderFeeIntVal,getUserAgent(request),orderCode,weChatUtil);
    	return ResponseEntity.ok(wxOrderSignStr);
    }

    @GetMapping("/rechargepay/sign")
    @ApiOperation(value = "微信充值支付签名", response = String.class)
    public ResponseEntity<String> rechargepaySign(HttpServletRequest request) throws Exception {
        String openIdAfterDm5=request.getParameter("openIdAfterDm5");
        String orderFee=request.getParameter("orderFee");
        int orderFeeIntVal=Integer.valueOf(orderFee);
        //通过加密后的openIdAfterDm5查找数据库openId
        String openId="ouxJf0RQY0eCN6OWotvzwulqSFJQ";
        String rechargeCode="4444444"+System.currentTimeMillis();
        String wxRechargeSignStr=payService.wxRechargePay(getRemoteAddr(request),openId,orderFeeIntVal,getUserAgent(request),rechargeCode,weChatUtil);
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
