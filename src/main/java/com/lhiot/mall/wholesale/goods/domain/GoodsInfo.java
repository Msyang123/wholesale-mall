package com.lhiot.mall.wholesale.goods.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class GoodsInfo {

    @JsonProperty("id")
    private Long id;

    @ApiModelProperty(notes = "商品规格id", dataType = "Long")
    private Long standardId;
    
    @JsonProperty("name")
    private String goodsName;
    
    @JsonProperty("price")
    private Integer price;

    @JsonProperty("minPrice")
    private Integer minPrice;

    @JsonProperty("maxPrice")
    private Integer maxPrice;

    @JsonProperty("unit")
    private String unitName;

    @JsonProperty("standard")
    private String standard;

    @JsonProperty("stock")
    private Integer stockLimit;

    @JsonProperty("src")
    private String goodsImage;

    @JsonProperty("desc")
    private String goodsDes;

    @JsonProperty("extraSrc")
    private String otherImage;

    @JsonProperty("goodsStandardId")
    private Long goodsStandardId;

    @JsonProperty("flashPrice")
    private Long specialPrice;

    @JsonProperty("saleCount")
    private Integer saleCount;

    @JsonProperty("content")
    private String goodsImages;

    @JsonProperty("standardList")
    private List<GoodsPriceRegion> goodsPriceRegionList;
}
