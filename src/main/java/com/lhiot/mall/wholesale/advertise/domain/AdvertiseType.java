package com.lhiot.mall.wholesale.advertise.domain;

public enum AdvertiseType {
	popup("首页弹窗"),
	top("顶部轮播图"),
	flashsale("限时抢购"),
	bottom("底部banner图");
	
	private final String displayTag;
	
	public String getDisplayTag(){
		return this.displayTag;
	}
	
	AdvertiseType(String displayTag){
		this.displayTag = displayTag;
	}
}
