package com.lhiot.mall.wholesale.activity.domain;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	
	@ApiModelProperty(notes="是否有效",dataType="String")
	private String vaild;
	
	@ApiModelProperty(notes="抢购活动商品",dataType="java.util.List")
	List<FlashsaleGoods> flashGoods;
}
