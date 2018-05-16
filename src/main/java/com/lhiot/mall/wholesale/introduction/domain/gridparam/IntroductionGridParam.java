package com.lhiot.mall.wholesale.introduction.domain.gridparam;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.leon.microx.common.wrapper.PageObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * Created by HuFan on 2018/4/23.
 */
@Data
@ToString
@ApiModel
public class IntroductionGridParam extends PageObject {

    @ApiModelProperty(notes="服务类型",dataType="String")
    private String serviceType;

    @ApiModelProperty(notes="创建时间起",dataType="String")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTimeBegin;

    @ApiModelProperty(notes="创建时间止",dataType="String")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTimeEnd;

    @ApiModelProperty(notes="分页查询开始页面",dataType="Integer")
    private Integer start;

    @ApiModelProperty(notes="页数",dataType="Integer")
    private Integer page;

    @ApiModelProperty(notes="每页行数",dataType="Integer")
    private Integer rows;

    @ApiModelProperty(notes="排序字段",dataType="String")
    private String sidx;

    @ApiModelProperty(notes="排序",dataType="String")
    private String sord;


}
