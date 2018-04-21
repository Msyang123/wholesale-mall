package com.lhiot.mall.wholesale.demandgoods.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by HuFan on 2018/4/17
 */
@Data
@ToString
@ApiModel@NoArgsConstructor
public class DemandGoods {

    @ApiModelProperty(notes="新品需求ID",dataType="Long")
    private long id;

    @ApiModelProperty(notes="商品名称",dataType="String")
    private String goodsName;

    @ApiModelProperty(notes="商品品牌",dataType="String")
    private String goodsBrand;

    @ApiModelProperty(notes="商品规格",dataType="String")
    private String goodsStandard;

    @ApiModelProperty(notes="参考价格",dataType="Int")
    private int referencePrice;

    @ApiModelProperty(notes="供应商",dataType="String")
    private String supplier;

    @ApiModelProperty(notes="备注",dataType="String")
    private String comments;

    @ApiModelProperty(notes="联系方式",dataType="String")
    private String contactWay;

    @ApiModelProperty(notes="用户ID",dataType="Long")
    private String userId;

    //TODO dataType id datetime
    @ApiModelProperty(notes="提交时间",dataType="String")
    private String createTime;

    //--------t_whs_user 用户表-------
    @ApiModelProperty(notes="店铺名称",dataType="String")
    private String shopName;

    @ApiModelProperty(notes="店长名",dataType="String")
    private String userName;

    @ApiModelProperty(notes="手机号码",dataType="String")
    private String phone;
}
