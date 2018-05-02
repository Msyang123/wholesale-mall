package com.lhiot.mall.wholesale.pay.domain;


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
    private long id;

    @JsonProperty("orderId")
    private long orderId;

    @JsonProperty("orderCode")
    private String orderCode;

    @JsonProperty("userId")
    private long userId;

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("refundFee")
    private int refundFee;

    @JsonProperty("totalFee")
    private int totalFee;

    @JsonProperty("bankType")
    private String bankType;

    @JsonProperty("bankName")
    private String bankName;

    @JsonProperty("paymentFrom")
    private int paymentFrom;

    @JsonProperty("paymentStep")
    private int paymentStep;

    @JsonProperty("paymentOrderType")
    private int paymentOrderType;

    @JsonProperty("paymentTime")
    private Timestamp paymentTime;

}
