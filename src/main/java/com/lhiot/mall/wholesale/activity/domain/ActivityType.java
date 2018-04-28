package com.lhiot.mall.wholesale.activity.domain;

public enum ActivityType {
	flashsale("限时抢购"),
	register("注册送礼");
	
	private final String displayTag;
	
	public String getDisplayTag(){
		return this.displayTag;
	}
	
	ActivityType(String displayTag){
		this.displayTag = displayTag;
	}
}
