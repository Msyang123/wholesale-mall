package com.lhiot.mall.wholesale.coupon.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("活动配置或者领取的优惠券")
@Data
public class ActivityCoupon {

	@ApiModelProperty(notes="奖励配置id",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="优惠券配置编号",dataType="Long")
	private Long couponConfigId;
	
	@ApiModelProperty(notes="优惠券金额",dataType="Integer")
	private Integer couponFee;
	
	@ApiModelProperty(notes="满减金额",dataType="Integer")
	private Integer fullFee;
	
	@ApiModelProperty(notes="有效天数",dataType="Integer")
	private Integer vaildDays;
	
	@ApiModelProperty(notes="奖励数量",dataType="Integer")
	private Integer rewardAmount;
}
