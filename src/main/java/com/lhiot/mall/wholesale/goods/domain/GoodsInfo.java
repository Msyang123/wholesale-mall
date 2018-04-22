package com.lhiot.mall.wholesale.goods.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lhiot.mall.wholesale.user.domain.SalesUserRelation;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
