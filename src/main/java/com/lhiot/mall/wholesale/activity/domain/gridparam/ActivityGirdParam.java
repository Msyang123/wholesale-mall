package com.lhiot.mall.wholesale.activity.domain.gridparam;

import com.leon.microx.common.wrapper.PageObject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel(description = "分页查询的参数")
@NoArgsConstructor
public class ActivityGirdParam extends PageObject {

	@ApiModelProperty(notes="活动起始时间",dataType="Long")
	private Long activityId;
	
	@ApiModelProperty(notes="活动类型",dataType="String")
	private String activityType;
	
	@ApiModelProperty(notes="活动开启时间起",dataType="String")
	private String beginTime;
	
	@ApiModelProperty(notes="活动开始止",dataType="String")
	private String endTime;
	
	@ApiModelProperty(notes="是否有效",dataType="String")
	private String vaild;

	@ApiModelProperty(notes="商品单位名称",dataType="Integer")
	private Integer start;
}
