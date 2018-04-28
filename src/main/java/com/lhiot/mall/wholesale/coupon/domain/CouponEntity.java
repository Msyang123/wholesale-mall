package com.lhiot.mall.wholesale.coupon.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class CouponEntity {

	@ApiModelProperty(notes="ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="优惠券配置编号",dataType="Long")
	private Long couponConfigId;
	
	@ApiModelProperty(notes="用户编号",dataType="Long")
	private Long userId;
	
	@ApiModelProperty(notes="有效时间",dataType="String")
	private String effectiveTime;
	
	@ApiModelProperty(notes="失效时间",dataType="String")
	private String failureTime;
	
	@ApiModelProperty(notes="优惠券状态：unuse-未使用 use-已使用 expire-已过期",dataType="String")
	private String couponStatus;
	
	@ApiModelProperty(notes="获取时间",dataType="String")
	private String getTime;
	
	@ApiModelProperty(notes="使用时间",dataType="String")
	private String useTime;
	
	@ApiModelProperty(notes="优惠券金额",dataType="String")
	private String couponFee;
	
	@ApiModelProperty(notes="满减金额",dataType="String")
	private String fullFee;
}
