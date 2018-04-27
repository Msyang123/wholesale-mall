package com.lhiot.mall.wholesale.coupon.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserCouponParam {

	@ApiModelProperty(notes="用户id",dataType="Long")
	private Long userId;
	
	@ApiModelProperty(notes="用户id",dataType="String")
	private String couponStatus;
	
	@ApiModelProperty(notes="订单金额",dataType="Integer")
	private Integer orderFee;
	
	@ApiModelProperty(notes="第几页",dataType="Integer")
	private Integer page;
	
	@ApiModelProperty(notes="每页展示的行数",dataType="Integer")
	private Integer rows;
	
	@ApiModelProperty(notes="起始行",dataType="Integer")
	private Integer start;
}
