package com.lhiot.mall.wholesale.aftersale.domain;

public enum ApplicationType {
	consult("协调处理"),
	refund("退货"),
	supplement("补差价");
	
	private final String displayTag;
	
	public String getDisplayTag(){
		return this.displayTag;
	}
	
	ApplicationType(String displayTag){
		this.displayTag = displayTag;
	}
}
