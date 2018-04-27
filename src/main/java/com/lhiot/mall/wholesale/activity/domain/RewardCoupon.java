package com.lhiot.mall.wholesale.activity.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class RewardCoupon {

	@ApiModelProperty(notes="活动ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="活动ID",dataType="Long")
	private Long activityId;
	
	@ApiModelProperty(notes="活动起始时间",dataType="String")
	private String startTime;
	
	@ApiModelProperty(notes="活动结束时间",dataType="String")
	private String endTime;
	
	@ApiModelProperty(notes="活动类型",dataType="String")
	private String activityType;
	
	@ApiModelProperty(notes="优惠券配置Id",dataType="Long")
	private Long couponConfigId;
	
	@ApiModelProperty(notes="优惠券奖励数量",dataType="Integer")
	private Integer rewardAmount;
	
	@ApiModelProperty(notes="优惠名称",dataType="Long")
	private Long couponName;
	
	@ApiModelProperty(notes="优惠券面值",dataType="Integer")
	private Integer couponFee;
	
	@JsonIgnore
	@ApiModelProperty(notes="优惠券面值，用户后台管理，冗余字段",dataType="String")
	private String couponFeeDisplay;
	
	@ApiModelProperty(notes="优惠券面值",dataType="Integer")
	private Integer fullFee;
	
	@JsonIgnore
	@ApiModelProperty(notes="面减金额，用户后台管理，冗余字段",dataType="String")
	private String fullFeeDispaly;
	
	@ApiModelProperty(notes="优惠券种类 all-全品类 single-单品类",dataType="String")
	private String couponType;

}
