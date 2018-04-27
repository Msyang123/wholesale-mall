package com.lhiot.mall.wholesale.activity.domain;

public enum ActivityPeriodsType {
	current("当期"),
	next("下期");
	
	private final String displayTag;
	
	public String getDisplayTag(){
		return this.displayTag;
	}
	
	ActivityPeriodsType(String displayTag){
		this.displayTag = displayTag;
	}
}
