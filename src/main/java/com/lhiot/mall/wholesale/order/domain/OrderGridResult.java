package com.lhiot.mall.wholesale.order.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Created by HuFan on 2018/4/25.
 */
@Data
@ApiModel
@NoArgsConstructor
public class OrderGridResult {
    @JsonProperty("id")
    private long id;

    @JsonProperty("orderCode")
    private String orderCode;

    @JsonProperty("createTime")
    private String createTime;

    @JsonProperty("totalFee")
    private long totalFee;

    @JsonProperty("discountFee")
    private long discountFee;

    @JsonProperty("payableFee")
    private long payableFee;

    @JsonProperty("settlementType")
    private String settlementType;

    @JsonProperty("userId")
    private long userId;

    @JsonProperty("paymentTime")
    private String paymentTime;

    @JsonProperty("shopName")
    private String shopName;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("orderStatus")
    private String orderStatus;
}
