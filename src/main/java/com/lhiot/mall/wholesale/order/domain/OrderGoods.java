package com.lhiot.mall.wholesale.order.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class OrderGoods {
    @JsonProperty("id")
    private long id;

    @JsonProperty("name")
    private String goodsName;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("suttle")
    private int weight;

    @JsonProperty("price")
    private int price;

    @JsonProperty("src")
    private String goodsImage;

    @JsonProperty("num")
    private int number;
}
