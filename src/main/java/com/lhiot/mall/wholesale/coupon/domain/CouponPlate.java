package com.lhiot.mall.wholesale.coupon.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class CouponPlate {

	@ApiModelProperty(notes="ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="优惠名称",dataType="String")
	private String couponName;
	
	@ApiModelProperty(notes="优惠券面值",dataType="Integer")
	private Integer couponFee;
	
	@ApiModelProperty(notes="优惠券面值，用户后台管理，冗余字段",dataType="String")
	private String couponFeeDisplay;
	
	@ApiModelProperty(notes="优惠券面值",dataType="Integer")
	private Integer fullFee;
	
	@ApiModelProperty(notes="面减金额，用户后台管理，冗余字段",dataType="String")
	private String fullFeeDispaly;
	
	@ApiModelProperty(notes="优惠券种类 all-全品类 single-单品类",dataType="String")
	private String couponType;
	
	@ApiModelProperty(notes="是否有效 true无效 false有效",dataType="String")
	private String vaild;
}
