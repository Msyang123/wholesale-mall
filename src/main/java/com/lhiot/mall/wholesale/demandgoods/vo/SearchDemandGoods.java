package com.lhiot.mall.wholesale.demandgoods.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * Created by HuFan on 2018/4/17.
 */
@Data
@ToString
@ApiModel
public class SearchDemandGoods {
    @ApiModelProperty(notes="手机号",dataType="String")
    private String phone;

    @ApiModelProperty(notes="用户名",dataType="String")
    private String nickName;

}
