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
public class PaymentLog {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("orderId")
    private Long orderId;

    @JsonProperty("orderCode")
    private String orderCode;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("refundFee")
    private Integer refundFee;

    @JsonProperty("totalFee")
    private Integer totalFee;

    @JsonProperty("bankType")
    private String bankType;

    @JsonProperty("bankName")
    private String bankName;

    @JsonProperty("paymentFrom")
    private String paymentFrom;

    @JsonProperty("paymentStep")
    private String paymentStep;

    @JsonProperty("paymentOrderType")
    private String paymentOrderType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @JsonProperty("paymentTime")
    private Timestamp paymentTime;

}
