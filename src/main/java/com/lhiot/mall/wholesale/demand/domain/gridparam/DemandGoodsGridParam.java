package com.lhiot.mall.wholesale.demand.domain.gridparam;

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
public class DemandGoodsGridParam extends PageObject {

    @ApiModelProperty(notes="手机号或用户名",dataType="String")
    private String namePhone;

    @ApiModelProperty(notes="创建时间起",dataType="String")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTimeBegin;

    @ApiModelProperty(notes="创建时间止",dataType="String")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTimeEnd;

    @ApiModelProperty(notes="用户ids",dataType="Long")
    private Long[] userIds;

    @ApiModelProperty(notes="分页查询开始页面",dataType="Integer")
    private Integer start;

    @ApiModelProperty(notes="页数",dataType="Integer")
    private Integer page;

    @ApiModelProperty(notes="每页行数",dataType="Integer")
    private Integer rows;

    @ApiModelProperty(notes="索引",dataType="String")
    private String sidx;

    @ApiModelProperty(notes="排序",dataType="String")
    private String sord;

    /*public void setUserIds(String userIds){
        String[] array = StringUtils.tokenizeToStringArray(userIds, ",");
        if (!ObjectUtils.isEmpty(array)){
            this.userIds = new Long[array.length];
            for (int i = 0; i < array.length; i++) {
                this.userIds[i] = Long.valueOf(array[i]);
            }
        }
    }*/


}
