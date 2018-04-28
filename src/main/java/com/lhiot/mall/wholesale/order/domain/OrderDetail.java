package com.lhiot.mall.wholesale.order.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @JsonProperty("needPay")
    private Integer needPay;

    @JsonProperty("payType")
    private Integer orderType;

    @JsonProperty("status")
    private Integer orderStatus;

    @JsonProperty("auditStatus")
    private Integer checkStatus;

    @JsonProperty("createTime")
    private String createTime;

    @JsonProperty("total")
    private Integer total;

    @JsonProperty("couponVal")
    private Integer orderDiscountFee;

    @JsonProperty("userId")
    private long userId;

    @JsonProperty("payStatus")
    private Integer payStatus;

    @JsonProperty("comments")
    private String remarks;

    @JsonProperty("proList")
    private List<OrderGoods> orderGoodsList;

    @JsonProperty("shopName")
    private String shopName;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("phone")
    private String phone;

}
