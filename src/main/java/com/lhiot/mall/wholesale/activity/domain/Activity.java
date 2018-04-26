package com.lhiot.mall.wholesale.activity.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

@Data
@ApiModel
@NoArgsConstructor
public class Activity {

	@ApiModelProperty(notes="活动ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="活动描述",dataType="String")
	private String activityDesc;
	
	@ApiModelProperty(notes="活动起始时间",dataType="String")
	private String startTime;
	
	@ApiModelProperty(notes="活动结束时间",dataType="String")
	private String endTime;
	
	@ApiModelProperty(notes="活动类型",dataType="String")
	private String activityType;
	
	@ApiModelProperty(notes="链接地址",dataType="String")
	private String linkUrl;
	
	@ApiModelProperty(notes="是否有效",dataType="Boolean")
	private Boolean vaild;
	
	@JsonIgnore
	@ApiModelProperty(notes="是否有效,冗余字段用于维护数据",dataType="Integer")
	private Integer vaildInt;
}
