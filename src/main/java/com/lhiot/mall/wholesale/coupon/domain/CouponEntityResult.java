package com.lhiot.mall.wholesale.coupon.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class CouponEntityResult {

	@ApiModelProperty(notes="ID",dataType="Long")
	private Long id;
	
	@ApiModelProperty(notes="优惠券配置编号",dataType="Long")
	private Long couponConfigId;
	
	@ApiModelProperty(notes="优惠券名称",dataType="String")
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
	private int couponFee;
	
	@ApiModelProperty(notes="满减金额",dataType="Integer")
	private int fullFee;
	
	@ApiModelProperty(notes="用户名称",dataType="String")
	private String userName;
	
	@ApiModelProperty(notes="会员状态 0临时会员 1正式会员（审核通过）",dataType="Integer")
	private String userStatus;
	
	@ApiModelProperty(notes="用户状态",dataType="String")
	private String phone;
	
	@ApiModelProperty(notes="店名",dataType="String")
	private String shopName;
	
	@ApiModelProperty(notes="详细地址",dataType="String")
	private String addressDetail;
	
	@ApiModelProperty(notes="优惠券来源",dataType="String")
	private String couponFrom;
}
