package com.lhiot.mall.wholesale.demand.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lhiot.mall.wholesale.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by HuFan on 2018/4/17
 */
@Data
@ToString
@ApiModel@NoArgsConstructor
public class DemandGoods {

    @ApiModelProperty(notes = "新品需求ID", dataType = "Long")
    private long id;

    @ApiModelProperty(notes = "商品名称", dataType = "String")
    private String goodsName;

    @ApiModelProperty(notes = "商品品牌", dataType = "String")
    private String goodsBrand;

    @ApiModelProperty(notes = "商品规格", dataType = "String")
    private String goodsStandard;

    @ApiModelProperty(notes = "参考价格", dataType = "Int")
    private int referencePrice;

    @ApiModelProperty(notes = "供应商", dataType = "String")
    private String supplier;

    @ApiModelProperty(notes = "备注", dataType = "String")
    private String comments;

    @ApiModelProperty(notes = "联系方式", dataType = "String")
    private String contactPhone;

    @ApiModelProperty(notes = "用户ID", dataType = "Long")
    private Long userId;

    @ApiModelProperty(notes = "创建时间", dataType = "String")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;



}
