package com.lhiot.mall.wholesale.pay.api;

import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.invoice.domain.Invoice;
import com.lhiot.mall.wholesale.invoice.service.InvoiceService;
import com.lhiot.mall.wholesale.order.domain.DebtOrder;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.service.DebtOrderService;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.pay.service.PayService;
import com.lhiot.mall.wholesale.pay.service.PaymentLogService;
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
import java.util.Objects;

@Slf4j
@Api(description = "微信支付接口")
@RestController
@RequestMapping("/wechat")
public class WxPayApi {
	
	private final PayService payService;

    private final PaymentLogService paymentLogService;
    private final OrderService orderService;
    private final DebtOrderService debtOrderService;

    private final InvoiceService invoiceService;
	private final WeChatUtil weChatUtil;

    private final SnowflakeId snowflakeId;



	@Autowired
	public WxPayApi(PayService payService,PaymentLogService paymentLogService,OrderService orderService,DebtOrderService debtOrderService,InvoiceService invoiceService, PaymentProperties properties,SnowflakeId snowflakeId){

        this.payService = payService;
        this.paymentLogService=paymentLogService;
        this.orderService=orderService;
        this.debtOrderService=debtOrderService;
        this.invoiceService=invoiceService;
        this.weChatUtil = new WeChatUtil(properties);
        this.snowflakeId=snowflakeId;
	}
	
    @GetMapping("/orderpay/sign")
    @ApiOperation(value = "微信订单支付签名", response = String.class)
    public ResponseEntity<String> orderpaySign(HttpServletRequest request,@RequestParam("openId") String openId,@RequestParam("orderCode") String orderCode) throws Exception {
        OrderDetail orderDetail= orderService.searchOrder(orderCode);
	    //通过加密后的openIdAfterDm5查找数据库openId
        String wxOrderSignStr=payService.wxOrderPay(getRemoteAddr(request),openId,
                orderDetail.getPayableFee()+orderDetail.getDeliveryFee(),getUserAgent(request),orderCode,weChatUtil);
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

    @GetMapping("/invoicepay/sign")
    @ApiOperation(value = "微信发票支付签名", response = String.class)
    public ResponseEntity<String> invoicepaySign(HttpServletRequest request,@RequestParam("openId") String openId,@RequestParam("invoiceCode") String invoiceCode) throws Exception {
        //依据发票业务编码查询发票信息
        Invoice invoice= invoiceService.findInvoiceByCode(invoiceCode);
        if(Objects.isNull(invoice)){
            return ResponseEntity.badRequest().body("未找到开票信息");
        }else if(invoice.getInvoiceStatus()==1){
            return ResponseEntity.badRequest().body("已经开票，请勿重复支付");
        }
        //查询支付记录 防止重复支付
        PaymentLog paymentLog=paymentLogService.getPaymentLog(invoiceCode);
        //判断重复支付
        if(Objects.nonNull(paymentLog)){
            return ResponseEntity.badRequest().body("已经支付，请勿重复支付");
        }

        String wxInvoiceSignStr=payService.wxInvoicePay(getRemoteAddr(request),openId,invoice.getTaxFee(),getUserAgent(request),invoiceCode,weChatUtil);
        //FIXME 写发票签名日志
        return ResponseEntity.ok(wxInvoiceSignStr);
    }

    @PostMapping("/invoice/notify")
    @ApiOperation(value = "微信发票支付回调", response = String.class)
    public ResponseEntity<String> invoiceNotify(HttpServletRequest request) throws Exception {
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

    @GetMapping("/offlinepay/sign")
    @ApiOperation(value = "账款付款微信支付签名", response = String.class)
    public ResponseEntity<String> debtorderpaySign(HttpServletRequest request,@RequestParam("openId") String openId,@RequestParam("orderDebtCode") String orderDebtCode) throws Exception {
        //依据欠款订单业务编码查询欠款订单信息
        DebtOrder debtOrder= debtOrderService.findByCode(orderDebtCode);
        //审核状态 0-未支付 1-审核中 2-审核失败 3-已支付
        if(Objects.isNull(debtOrder)){
            return ResponseEntity.badRequest().body("未找到欠款订单信息");
        }else if(debtOrder.getCheckStatus()==1){
            return ResponseEntity.badRequest().body("欠款订单审核中");
        }else if(debtOrder.getCheckStatus()==3){
            return ResponseEntity.badRequest().body("欠款订单已支付");
        }

        String wxInvoiceSignStr=payService.wxDebtopderPay(getRemoteAddr(request),openId,debtOrder.getDebtFee(),getUserAgent(request),debtOrder.getOrderDebtCode(),weChatUtil);
        //FIXME 写欠款订单支付签名日志
        return ResponseEntity.ok(wxInvoiceSignStr);
    }

    @PostMapping("/offline/notify")
    @ApiOperation(value = "账款付款微信支付回调", response = String.class)
    public ResponseEntity<String> debtorderNotify(HttpServletRequest request) throws Exception {
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
            //FIXME 需要依据欠款订单将订单状态改成已经支付
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
