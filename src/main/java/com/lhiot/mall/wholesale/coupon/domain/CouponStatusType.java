package com.lhiot.mall.wholesale.coupon.domain;

public enum CouponStatusType {
	unuse("未使用"),
	use("已使用"),
	expire("已过期");
	
	private final String displayTag;
	
	public String getDisplayTag(){
		return this.displayTag;
	}
	
	CouponStatusType(String displayTag){
		this.displayTag = displayTag;
	}
}
