package com.lhiot.mall.wholesale.coupon.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	
	@JsonIgnore
	@ApiModelProperty(notes="是否有效 0无效 1有效,冗余字段",dataType="Integer")
	private Integer vaildInt;
	
	@ApiModelProperty(notes="是否有效 true无效 false有效",dataType="Boolean")
	private Boolean vaild;
}
