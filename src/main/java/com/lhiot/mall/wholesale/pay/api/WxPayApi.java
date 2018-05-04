package com.lhiot.mall.wholesale.pay.api;

import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.invoice.domain.Invoice;
import com.lhiot.mall.wholesale.invoice.domain.InvoiceTitle;
import com.lhiot.mall.wholesale.invoice.service.InvoiceService;
import com.lhiot.mall.wholesale.order.domain.DebtOrder;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.service.DebtOrderService;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
import com.lhiot.mall.wholesale.pay.service.PayService;
import com.lhiot.mall.wholesale.pay.service.PaymentLogService;
import com.lhiot.mall.wholesale.user.domain.User;
import com.lhiot.mall.wholesale.user.service.UserService;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
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
    
    private final UserService userService;



	@Autowired
	public WxPayApi(PayService payService,PaymentLogService paymentLogService,OrderService orderService,DebtOrderService debtOrderService,InvoiceService invoiceService, PaymentProperties properties,SnowflakeId snowflakeId,
                    UserService userService){

        this.payService = payService;
        this.paymentLogService=paymentLogService;
        this.orderService=orderService;
        this.debtOrderService=debtOrderService;
        this.invoiceService=invoiceService;
        this.weChatUtil = new WeChatUtil(properties);
        this.snowflakeId=snowflakeId;
        this.userService= userService;
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
    public ResponseEntity<String> invoicepaySign(HttpServletRequest request,@RequestParam("openId") String openId,@RequestParam("orderCodes") String orderCodes) throws Exception {
        //TODO 依据orderCodes 查询发票信息 拒绝重复开票
        for(String item:orderCodes.split(",")){
            Invoice invoice= invoiceService.listByorderCodesLike(orderCodes);
            if(Objects.nonNull(invoice)){
                return ResponseEntity.badRequest().body("订单编码("+item+")已经开票，请勿重复开票");
            }
        }
        //查询所有的订单计算税费
        int orderTotal=100;
        int taxFee=new BigDecimal(orderTotal).multiply(new BigDecimal(0.0336)).setScale(0, RoundingMode.DOWN).intValue();
        String wxInvoiceSignStr=payService.wxInvoicePay(getRemoteAddr(request),openId,taxFee,getUserAgent(request),snowflakeId.stringId(),"附加发票相关信息包括发票抬头信息",weChatUtil);
        //回调再写发票信息到数据库中
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
            String attach = wrap.get("attach");
            String openid= wrap.get("openid");
            String totalFee = wrap.get("total_fee");
            int fee = Integer.parseInt(totalFee);
            //获取传达的附加参数获取发票信息
            log.info("attach:"+attach+"fee:"+fee);
            long nvoiceTitleId=Long.valueOf(attach);
            User user= userService.searchUserByOpenid(openid);
            //依据附加参数查询发票抬头信息
            InvoiceTitle invoiceTitle= invoiceService.selectInvoiceTitle(nvoiceTitleId);
            //创建发票信息
            Invoice invoice=new Invoice();
                    
            invoice.setInvoiceTitleId(invoiceTitle.getId());
            invoice.setTaxpayerNumber(invoiceTitle.getTaxpayerNumber());
            invoice.setCompanyName(invoiceTitle.getCompanyName());
            invoice.setContactName(invoiceTitle.getContactName());
            invoice.setContactPhone(invoiceTitle.getContactPhone());
           /* invoice.setInvoiceFee();
            invoice.setInvoiceTax();*/
            invoice.setTaxFee(fee);
            invoice.setAddressArea(invoiceTitle.getAddressArea());
            invoice.setAddressDetail(invoiceTitle.getAddressDetail());
            invoice.setBankName(invoiceTitle.getBankName());
            invoice.setBankCardCode(invoiceTitle.getBankCardCode());
            invoice.setCreateTime(new Timestamp(System.currentTimeMillis()));
            /*invoice.setInvoiceOrderIds();
            invoice.setInvoiceCode();*/
            invoice.setUserId(user.getId());
                    
            int result=invoiceService.applyInvoice(invoice);
            boolean myDoIsOk=result>0;//我们处理的业务
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
        }else if(Objects.equals(debtOrder.getCheckStatus(),"unaudited")){
            return ResponseEntity.badRequest().body("欠款订单审核中");
        }else if(Objects.equals(debtOrder.getCheckStatus(),"paid") || Objects.equals(debtOrder.getCheckStatus(),"agree")){
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
