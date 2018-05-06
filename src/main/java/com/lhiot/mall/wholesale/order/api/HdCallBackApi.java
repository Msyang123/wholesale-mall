package com.lhiot.mall.wholesale.order.api;

import com.lhiot.mall.wholesale.MQDefaults;
import com.lhiot.mall.wholesale.order.domain.OrderDetail;
import com.lhiot.mall.wholesale.order.service.OrderService;
import com.sgsl.util.IOUtils;
import com.sgsl.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@Api(description = "海鼎回调")
@RequestMapping("/hd")
@Validated
public class HdCallBackApi {


    @Autowired
    private RabbitTemplate rabbit;
    @Autowired
    private OrderService orderService;


    @Deprecated
    @ApiOperation("海鼎订阅消息回调")
    @PostMapping("/call/back")
    public void callBack(HttpServletRequest request) {
        log.info("海鼎回调");
        try {
            InputStream inputStream = request.getInputStream();
            // 转换成utf-8格式输出
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            List<String> lst = IOUtils.readLines(in);
            IOUtils.closeQuietly(inputStream);
            String resultStr = StringUtils.join( "",lst);
            log.info("backOrder-jsonData:" + resultStr);

            log.info("传入JSON字符串：" + resultStr);
            JSONObject getJsonVal = new JSONObject(resultStr);
            JSONObject content = new JSONObject(getJsonVal.getString("content"));

            log.info("进行转义后的字符串 resultStr " + resultStr);
            log.info("content = " + content.toString());
            String orderCode = content.getString("front_order_id");
            // 依据订单编码查询订单
            OrderDetail order= orderService.searchOrder(orderCode);

            rabbit.convertAndSend(MQDefaults.DIRECT_EXCHANGE_NAME, "retry-order-resendhd", "延迟信息发送测试",
                    message -> {
                        // 10秒钟
                        message.getMessageProperties().setExpiration("10000");
                        return message;
                    });

            //rabbit.convertAndSend(MQDefaults.MATCH_EXCHANGE_NAME, "im.push.route", "sddd测试的主题队列");
            // 所有订单推送类消息
            if ("order".equals(getJsonVal.getString("group")) && Objects.nonNull(order)) {
                // 订单备货
                if ("order.shipped".equals(getJsonVal.getString("topic"))) {
                    log.info("订单备货回调********");

                }// 可能海鼎返回结果已经改了
                else if ("return.received".equals(getJsonVal.getString("topic"))) {
                    log.info("订单退货回调*********");
                    // 海鼎退货完成,系统给用户退款

                } else {
                    log.info("hd other group message= " + getJsonVal.getString("group"));
                }
            }
        } catch (Exception e) {
            log.info("message = " + e.getMessage());
        }
    }
}
