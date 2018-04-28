package com.lhiot.mall.wholesale.order.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by HuFan on 2018/4/25.
 */
@Data
@ApiModel
@NoArgsConstructor
public class OrderGridResult {
    @JsonProperty("id")
    private long id;

    @JsonProperty("id")
    private String orderCode;

    @JsonProperty("orderCreateTime")
    private String orderCreateTime;

    @JsonProperty("orderTotal")
    private long orderTotal;

    @JsonProperty("orderDiscountFee")
    private long orderDiscountFee;

    @JsonProperty("order_need_fee")
    private long orderNeedFee;

    @JsonProperty("order_type")
    private long orderType;

    @JsonProperty("userId")
    private long userId;

    //------以下为管理系统所需数据-------
    //TODO 查询支付记录表支付时间
    @JsonProperty("paymentTime")
    private String paymentTime;

    @JsonProperty("shopName")
    private long shopName;

    @JsonProperty("userName")
    private long userName;

    @JsonProperty("phone")
    private long phone;

    @JsonProperty("orderStatus")
    private long orderStatus;
}
