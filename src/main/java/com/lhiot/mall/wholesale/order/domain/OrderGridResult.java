package com.lhiot.mall.wholesale.order.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private Long id;

    @JsonProperty("orderCode")
    private String orderCode;

    @JsonProperty("createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private String createTime;

    @JsonProperty("totalFee")
    private Long totalFee;

    @JsonProperty("discountFee")
    private Long discountFee;

    @JsonProperty("payableFee")
    private Long payableFee;

    @JsonProperty("settlementType")
    private String settlementType;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("paymentTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Timestamp paymentTime;

    @JsonProperty("shopName")
    private String shopName;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("orderStatus")
    private String orderStatus;
}
