package com.lhiot.mall.wholesale.coupon.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class CouponConfig {

	@ApiModelProperty(notes="ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="优惠券模块编号",dataType="Long")
	private Long couponId;
	
	@ApiModelProperty(notes="优惠券名称",dataType="String")
	private String couponName;
	
	@ApiModelProperty(notes="优惠券面值",dataType="String")
	private Integer couponFee;
	
	@ApiModelProperty(notes="满减金额",dataType="String")
	private Integer fullFee;
	
	@ApiModelProperty(notes="优惠券种类 all-全品类 single-单品类",dataType="String")
	private String couponType;
	
	@ApiModelProperty(notes="有效时间",dataType="String")
	private String effectiveTime;
	
	@ApiModelProperty(notes="失效时间",dataType="String")
	private String failureTime;
	
	@ApiModelProperty(notes="有效天数",dataType="Integer")
	private Integer vaildDays;
	
	@ApiModelProperty(notes="优惠券描述",dataType="String")
	private String couponDes;
}
