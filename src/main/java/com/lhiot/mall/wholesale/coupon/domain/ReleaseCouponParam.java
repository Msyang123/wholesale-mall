package com.lhiot.mall.wholesale.coupon.domain;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("手动发券参数")
public class ReleaseCouponParam {
	@ApiModelProperty(notes="用户的电话号码，逗号分割",dataType="String")
	@NotNull
	private String phones;
	
	@ApiModelProperty(notes="优惠券配置id，逗号分割",dataType="String")
	@NotNull
	private String couponConfigIds;
}
