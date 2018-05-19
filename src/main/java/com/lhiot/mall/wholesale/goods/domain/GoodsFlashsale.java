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
    private Integer specialPrice;

    @ApiModelProperty(notes = "限购数量", dataType = "Integer")
    @JsonProperty("limit")
    private Integer limitQuantity;

    @ApiModelProperty(notes = "活动编号", dataType = "Integer")
    @JsonProperty("activityId")
    private Long activityId;

    @ApiModelProperty(notes = "结束时间", dataType = "Timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @JsonProperty("endTime")
    private Timestamp endTime;

    @ApiModelProperty(notes = "开始时间", dataType = "Timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @JsonProperty("startTime")
    private Timestamp startTime;

    @ApiModelProperty(notes = "用户已购次数", dataType = "Integer")
    @JsonProperty("userPucharse")
    private Integer userPucharse;
}
