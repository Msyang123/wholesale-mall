package com.lhiot.mall.wholesale.aftersale.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lhiot.mall.wholesale.order.domain.OrderGoods;
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

    @JsonProperty("orderGoodsList")
    private List<OrderGoods> orderGoodsList;

    @JsonProperty("auditStatus")
    private String auditStatus;

}
