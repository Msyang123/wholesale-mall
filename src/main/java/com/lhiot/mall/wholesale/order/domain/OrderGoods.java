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
    private Long id;

    @JsonProperty("goodsStandardId")
    private Long goodsStandardId;

    @JsonProperty("orderId")
    private Long orderId;

    @JsonProperty("num")
    private int quanity;

    @JsonProperty("price")
    private Integer goodsPrice;

    @JsonProperty("paymentTime")
    private Timestamp paymentTime;

    @JsonProperty("refundStaus")
    private Integer refundStaus;

    @JsonProperty("discountGoodsPrice")
    private Integer discountGoodsPrice;

    @JsonProperty("userId")
    private Long userId;

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
