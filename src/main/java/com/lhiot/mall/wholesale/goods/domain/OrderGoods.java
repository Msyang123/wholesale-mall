package com.lhiot.mall.wholesale.goods.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@ApiModel
@NoArgsConstructor
public class OrderGoods {

    @JsonProperty("id")
    private long id;

    @JsonProperty("goodsStandardId")
    private long goodsStandardId;

    @JsonProperty("standardWeight")
    private int standardWeight;

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("quanity")
    private int quanity;

    @JsonProperty("goodsPrice")
    private int goodsPrice;

    @JsonProperty("paymentTime")
    private Timestamp paymentTime;

    @JsonProperty("isRefund")
    private int isRefund;

    @JsonProperty("discountGoodsPrice")
    private int discountGoodsPrice;

    @JsonProperty("userId")
    private int userId;

}
