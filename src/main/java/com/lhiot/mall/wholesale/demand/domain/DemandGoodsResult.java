package com.lhiot.mall.wholesale.demand.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


/**
 * Created by HuFan on 2018/4/23.
 */
@Data
@ApiModel
@NoArgsConstructor
public class DemandGoodsResult {

    @ApiModelProperty(notes = "新品需求ID", dataType = "Long")
    private Long id;

    @ApiModelProperty(notes = "商品名称", dataType = "String")
    private String goodsName;

    @ApiModelProperty(notes = "商品品牌", dataType = "String")
    private String goodsBrand;

    @ApiModelProperty(notes = "商品规格", dataType = "String")
    private String goodsStandard;

    @ApiModelProperty(notes = "参考价格", dataType = "Integer")
    private Integer referencePrice;

    @ApiModelProperty(notes = "供应商", dataType = "String")
    private String supplier;

    @ApiModelProperty(notes = "备注", dataType = "String")
    private String comments;

    @ApiModelProperty(notes = "联系方式", dataType = "String")
    private String contactPhone;

    @ApiModelProperty(notes = "用户ID", dataType = "Long")
    private Long userId;


    @ApiModelProperty(notes = "创建时间", dataType = "String")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private String createTime;

    //--------t_whs_user 用户表-------
    @ApiModelProperty(notes="店铺名称",dataType="String")
    private String shopName;

    @ApiModelProperty(notes="用户名称",dataType="String")
    private String userName;

    @ApiModelProperty(notes="手机号码",dataType="String")
    private String phone;

}
