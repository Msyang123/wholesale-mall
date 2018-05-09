package com.lhiot.mall.wholesale.coupon.domain;

public enum CouponType {
	artificial("手动发券"),
	activity("活动送券");
	
	private final String displayTag;
	
	public String getDisplayTag(){
		return this.displayTag;
	}
	
	CouponType(String displayTag){
		this.displayTag = displayTag;
	}
}
