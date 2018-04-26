package com.lhiot.mall.wholesale.pay.api;

import com.leon.microx.common.exception.ServiceException;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.lhiot.mall.wholesale.pay.domain.PaymentLog;
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
import java.util.Objects;

@Slf4j
@Api(description = "余额支付接口")
@RestController
@RequestMapping("/currency")
public class CurrencyPayApi {

	private final PayService payService;


    private final OrderService orderService;

	@Autowired
	public CurrencyPayApi(PayService payService, OrderService orderService){

        this.payService = payService;
        this.orderService=orderService;
	}
	
    @GetMapping("/orderpay/{orderCode}")
    @ApiOperation(value = "余额支付订单", response = String.class)
    public ResponseEntity<String> orderpay(@PathVariable("orderCode") String orderCode) {
        OrderDetail orderDetail = orderService.searchOrder(orderCode);
        if (Objects.isNull(orderDetail)){
            throw new ServiceException("没有该订单信息");
        }
        if(orderDetail.getOrderStatus()>2||orderDetail.getPayStatus()!=0){
            throw new ServiceException("已支付订单状态，请勿重复支付");
        }
        int payResult=payService.currencyPay(orderDetail);
        if(payResult>0){
            return ResponseEntity.ok(orderCode);
        }
        throw new ServiceException("余额支付订单失败");
    }


}
