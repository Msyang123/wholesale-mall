package com.lhiot.mall.wholesale.aftersale.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
@ApiModel
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OrderRefundApplication {

    @JsonProperty("id")
    private long id;

    @JsonProperty("refundType")
    private int refundType;

    @JsonProperty("existProblem")
    private int existProblem;

    @JsonProperty("otherProblem")
    private String otherProblem;

    @JsonProperty("refundEvidence")
    private String refundEvidence;

    @JsonProperty("verifyStatus")
    private int verifyStatus;

    @JsonProperty("orderDiscountFee")
    private int orderDiscountFee;

    @JsonProperty("deliveryFee")
    private int deliveryFee;

    @JsonProperty("comments")
    private String comments;

    @JsonProperty("orderId")
    private long orderId;

    @JsonProperty("userId")
    private long userId;
}
