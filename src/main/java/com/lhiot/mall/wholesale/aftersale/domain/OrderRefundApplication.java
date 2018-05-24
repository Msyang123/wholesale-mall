package com.lhiot.mall.wholesale.aftersale.domain;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@ApiModel
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OrderRefundApplication {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("applicationType")
    private String applicationType;

    @JsonProperty("existProblem")
    private String existProblem;

    @JsonProperty("otherProblem")
    private String otherProblem;

    @JsonProperty("refundEvidence")
    private String refundEvidence;

    @JsonProperty("orderDiscountFee")
    private Integer orderDiscountFee;

    @JsonProperty("deliveryFee")
    private Integer deliveryFee;

    @JsonProperty("comments")
    private String comments;

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("userId")
    private Long userId;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @JsonProperty("createTime")
    private Timestamp orderCreateTime;

    @JsonProperty("proList")
    private List<OrderGoods> orderGoodsList;
    //private OrderDetail orderDetail;

    @JsonProperty("auditStatus")
    private String auditStatus;

    @JsonProperty("afterStatus")
    private String afterStatus;

    @JsonProperty("contactsPhone")
    private String contactsPhone;

    @JsonProperty("start")
    private Integer start;

    @JsonProperty("rows")
    private Integer rows;

    private String sidx;

    private Integer page;

}
