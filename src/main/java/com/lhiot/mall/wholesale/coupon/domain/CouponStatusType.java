package com.lhiot.mall.wholesale.coupon.domain;

public enum CouponStatusType {
	unused("未使用"),
	used("已使用"),
	expired("已过期");
	
	private final String displayTag;
	
	public String getDisplayTag(){
		return this.displayTag;
	}
	
	CouponStatusType(String displayTag){
		this.displayTag = displayTag;
	}
}
