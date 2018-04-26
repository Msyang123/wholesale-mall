package com.lhiot.mall.wholesale.advertise.domain;

public enum AdvertiseType {
	popup("限时抢购"),
	sowing("轮播图"),
	flashSale("限时抢购"),
	banner("底部banner图");
	
	private final String displayTag;
	
	public String getDisplayTag(){
		return this.displayTag;
	}
	
	AdvertiseType(String displayTag){
		this.displayTag = displayTag;
	}
}
