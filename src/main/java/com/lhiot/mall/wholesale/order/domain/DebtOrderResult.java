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
public class DebtOrderResult {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("checkTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Timestamp checkTime;

    @JsonProperty("orderDebtCode")
    private String orderDebtCode;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Timestamp createTime;

    @JsonProperty("debtFee")
    private Integer debtFee;

    @JsonProperty("checkStatus")
    private String checkStatus;

    @JsonProperty("paymentType")
    private String paymentType;

    @JsonProperty("paymentEvidence")
    private String paymentEvidence;

    @JsonProperty("orderIds")
    private String orderIds;

    @JsonProperty("remarks")
    private String remarks;

    //用户表
    @JsonProperty("shopName")
    private String shopName;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("phone")
    private String phone;

    //订单表 根据账款订单的orderIds查询订单金额、折扣金额、应付金额、配送费
    @JsonProperty("totalFee")
    private Integer totalFee;

    @JsonProperty("discountFee")
    private Integer discountFee;

    @JsonProperty("payableFee")
    private Integer payableFee;

    @JsonProperty("deliveryFee")
    private Integer deliveryFee;

    //账款订单支付总金额
    @JsonProperty("payTotalFee")
    private Integer payTotalFee;

    @JsonProperty("refundFee")
    private Integer refundFee;
}
