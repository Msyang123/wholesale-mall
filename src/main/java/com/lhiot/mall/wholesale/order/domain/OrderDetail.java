package com.lhiot.mall.wholesale.order.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;

@Data
@ToString
@ApiModel
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OrderDetail {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("orderId")
    private String orderCode;

    @JsonProperty("salesmanId")
    private Long salesmanId;

    @JsonProperty("payType")
    private String settlementType;

    @JsonProperty("status")
    private String orderStatus;

    //订单当前状态 用于修改的时候约束条件
    @JsonProperty("currentOrderStatus")
    private String currentOrderStatus;

    @JsonProperty("auditStatus")
    private String checkStatus;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createTime")
    private Timestamp createTime;

    @JsonProperty("total")
    private Integer totalFee;

    @JsonProperty("couponVal")
    private Integer discountFee;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("payStatus")
    private String payStatus;

    @JsonProperty("comments")
    private String remarks;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonProperty("afterSaleTime")
    private Timestamp afterSaleTime;

    @JsonProperty("hdStatus")
    private String hdStatus;//发送订单到海鼎是否成功0成功1失败

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonProperty("deliveryTime")
    private Timestamp deliveryTime;

    @JsonProperty("deliveryFee")
    private Integer deliveryFee;

    @JsonProperty("deliveryAddress")
    private String deliveryAddress;

    @JsonProperty("proList")
    private List<OrderGoods> orderGoodsList;

    @JsonProperty("shopName")
    private String shopName;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("needPay")
    private  Integer payableFee;

    @JsonProperty("addressDetail")
    private String addressDetail;

    @JsonProperty("expire")
    private String expire;//是否已过期 yes no

    @JsonProperty("start")
    private Integer start;

    @JsonProperty("rows")
    private Integer rows;

    private String sidx;

    private Integer page;

}
