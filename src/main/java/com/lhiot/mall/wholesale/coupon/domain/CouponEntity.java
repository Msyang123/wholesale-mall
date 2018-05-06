package com.lhiot.mall.wholesale.coupon.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
	
	@JsonProperty("title")
	@ApiModelProperty(notes="优惠券名字",dataType="String")
	private String couponName;
	
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
	
	@ApiModelProperty(notes="优惠券金额",dataType="Integer")
	private Integer couponFee;
	
	@ApiModelProperty(notes="满减金额",dataType="Integer")
	private Integer fullFee;
	
	@JsonProperty("desc")
	@ApiModelProperty(notes="优惠券描述",dataType="String")
	private String couponDes;
	
	@ApiModelProperty(notes="是否可用",dataType="Boolean")
	private Boolean isValidate = false;
	
	@JsonIgnore
	@ApiModelProperty(notes="有效天数",dataType="Integer")
	private Integer vaildDays;
}
