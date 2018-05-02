package com.lhiot.mall.wholesale.goods.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@ApiModel
@NoArgsConstructor
public class GoodsFlashsale {
    @ApiModelProperty(notes = "抢购商品价格", dataType = "Integer")
    @JsonProperty("price")
    private int specialPrice;

    @ApiModelProperty(notes = "限购数量", dataType = "Integer")
    @JsonProperty("limit")
    private int limitQuantity;

    @ApiModelProperty(notes = "结束时间", dataType = "Timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("endTime")
    private Timestamp endTime;
}
