package com.lhiot.mall.wholesale.user.domain;

import com.leon.microx.common.wrapper.PageObject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ApiModel(description = "分页查询的参数")
@ToString
public class UserGridParam extends PageObject {
	@ApiModelProperty(notes="开始时间",dataType="String")
	private String beginTime;
	
	@ApiModelProperty(notes="结束时间",dataType="String")
	private String endTime;
	
	@ApiModelProperty(notes="用户电话",dataType="String")
	private String phone;
	
	@ApiModelProperty(notes="店长名称",dataType="String")
	private String userName;
	
	@ApiModelProperty(notes="业务员id",dataType="Long")
	private Long saleId;

	@ApiModelProperty(notes="查询起始页",dataType="Integer")
	private Integer start;
}
