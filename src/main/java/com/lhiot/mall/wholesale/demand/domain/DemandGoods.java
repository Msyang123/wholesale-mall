package com.lhiot.mall.wholesale.demand.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
@ApiModel
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DemandGoods {

    @JsonProperty("id")
    private long id;

    @JsonProperty("goodsName")
    private String goodsName;

    @JsonProperty("goodsBrand")
    private String goodsBrand;

    @JsonProperty("goodsStandard")
    private String goodsStandard;

    @JsonProperty("supplier")
    private String supplier;

    @JsonProperty("referencePrice")
    private int referencePrice;

    @JsonProperty("comments")
    private String comments;

    @JsonProperty("contactPhone")
    private String contactPhone;

    @JsonProperty("userId")
    private long userId;

    @JsonProperty("createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createTime;

}
