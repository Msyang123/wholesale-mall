package com.lhiot.mall.wholesale.goods.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class GoodsInfo {

    @JsonProperty("id")
    private long id;

    @JsonProperty("name")
    private String goodsName;

    @JsonProperty("price")
    private int price;

    @JsonProperty("minPrice")
    private int maxPrice;

    @JsonProperty("maxPrice")
    private int minPrice;

    @JsonProperty("unit")
    private String unitName;

    @JsonProperty("standard")
    private String standard;

    @JsonProperty("stock")
    private int stockLimit;

    @JsonProperty("src")
    private String goodsImage;

    @JsonProperty("desc")
    private String goodsDes;

    @JsonProperty("extraSrc")
    private String otherImage;

    @JsonProperty("goodsStandradId")
    private long goodsStandardId;

    @JsonProperty("flashPrice")
    private long specialPrice;

    @JsonProperty("saleCount")
    private int saleCount;

    @JsonProperty("standardList")
    private List<GoodsPriceRegion> goodsPriceRegionList;
}
