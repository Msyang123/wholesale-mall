package com.lhiot.mall.wholesale.goods.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class GoodsDetailResult {

    @ApiModelProperty(notes = "商品信息", dataType = "Goods")
    @JsonProperty("goods")
    private GoodsInfo goodsInfo;

    @ApiModelProperty(notes = "抢购信息", dataType = "GoodsFlashsale")
    @JsonProperty("flash")
    private GoodsFlashsale goodsFlashsale;


}
