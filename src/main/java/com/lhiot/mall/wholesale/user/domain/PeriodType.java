package com.lhiot.mall.wholesale.user.domain;

public enum PeriodType {
	current("本期"),
	last("上期");
	
	private final String displayTag;
	
	public String getDisplayTag(){
		return this.displayTag;
	}
	
	PeriodType(String displayTag){
		this.displayTag = displayTag;
	}
}
