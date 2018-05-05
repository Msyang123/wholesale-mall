package com.lhiot.mall.wholesale.order.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@ToString
@ApiModel
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OrderGoods {
    @JsonProperty("id")
    private long id;

    @JsonProperty("goodsStandardId")
    private long goodsStandardId;

    @JsonProperty("orderId")
    private long orderId;

    @JsonProperty("num")
    private int quanity;

    @JsonProperty("price")
    private int goodsPrice;

    @JsonProperty("paymentTime")
    private Timestamp paymentTime;

    @JsonProperty("refundStaus")
    private int refundStaus;

    @JsonProperty("discountGoodsPrice")
    private int discountGoodsPrice;

    @JsonProperty("userId")
    private long userId;

    @JsonProperty("name")
    private String goodsName;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("suttle")
    private BigDecimal standardWeight;

    @JsonProperty("src")
    private String goodsImage;

    @JsonProperty("hdSkuId")
    private String hdSkuId;
}
