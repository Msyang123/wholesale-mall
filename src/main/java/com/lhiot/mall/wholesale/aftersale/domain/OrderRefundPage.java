package com.lhiot.mall.wholesale.aftersale.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class OrderRefundPage {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("orderId")
    private String orderId;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @JsonProperty("createTime")
    private Timestamp createTime;

    @JsonProperty("totalFee")
    private Integer totalFee;

    @JsonProperty("discountFee")
    private Integer discountFee;

    @JsonProperty("payableFee")
    private Integer payableFee;

    @JsonProperty("paymentType")
    private String  paymentType;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @JsonProperty("paymentTime")
    private Timestamp paymentTime;

    @JsonProperty("payStatus")
    private String payStatus;

    @JsonProperty("shopName")
    private String shopName;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("orderStatus")
    private String orderStatus;

    @JsonProperty("hdStatus")
    private String hdStatus;

    @JsonProperty("applicationType")
    private String applicationType;//售后类型

    @JsonProperty("orderDiscountFee")
    private Integer orderDiscountFee;//优惠金额(退款金额)or补收差额

    @JsonProperty("deliveryFee")
    private Integer deliveryFee;

    @JsonProperty("auditStatus")
    private String auditStatus;

    @JsonProperty("deliveryFee")
    private Integer deliveryFee;//订单的配送费
    
    @JsonProperty("refundDeliveryFee")
    private Integer refundDeliveryFee;//售后退款的时候需要扣除的配送费
    
}
