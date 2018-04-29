package com.lhiot.mall.wholesale.order.domain;

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
    private long id;

    @JsonProperty("orderId")
    private String orderCode;

    @JsonProperty("salesmanId")
    private long salesmanId;

    @JsonProperty("payType")
    private Integer settlementType;

    @JsonProperty("status")
    private Integer orderStatus;

    //订单当前状态 用于修改的时候约束条件
    private Integer currentOrderStaus;

    @JsonProperty("auditStatus")
    private Integer checkStatus;

    @JsonProperty("createTime")
    private String createTime;

    @JsonProperty("total")
    private Integer totalFee;

    @JsonProperty("couponVal")
    private Integer discountFee;

    @JsonProperty("userId")
    private long userId;

    @JsonProperty("payStatus")
    private Integer payStatus;

    @JsonProperty("comments")
    private String remarks;

    @JsonProperty("afterSaleTime")
    private Timestamp afterSaleTime;

    @JsonProperty("hdStatus")
    private Integer hdStatus;//发送订单到海鼎是否成功0成功1失败

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

    @JsonProperty("payableFee")
    private  Integer payableFee;

}
