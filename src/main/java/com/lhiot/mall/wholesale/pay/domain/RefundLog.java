package com.lhiot.mall.wholesale.pay.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
@ApiModel
@NoArgsConstructor
public class RefundLog {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("transactionId")
    private String transactionId;//微信交易流水号

    @JsonProperty("refundFee")
    private Integer refundFee;//退支付金额

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @JsonProperty("refundTime")
    private Timestamp refundTime;//退款时间

    @JsonProperty("refundReason")
    private String refundReason;//退款原因

    @JsonProperty("paymentLogId")
    private Long paymentLogId;//支付日志ID

    @JsonProperty("refundType")
    private String refundType;//退款方式：balanceRefund-余额 wechatRefund-微信

    @JsonProperty("userId")
    private Long userId;//用户编号
}
