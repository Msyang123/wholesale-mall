package com.lhiot.mall.wholesale.activity.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class RewardCoupon {

	@ApiModelProperty(notes="奖励ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="活动id",dataType="Long")
	private Long activityId;
	
	@ApiModelProperty(notes="优惠券配置编号",dataType="Long")
	private Long couponConfigId;
	
	@ApiModelProperty(notes="奖励数量",dataType="Integer")
	private Integer rewardAmount;
	
	@ApiModelProperty(notes="优惠券名称",dataType="String")
	private String couponName;
	
	@ApiModelProperty(notes="优惠券金额",dataType="String")
	private String couponFee;
	
	@ApiModelProperty(notes="满减金额",dataType="String")
	private String fullFee;
}
