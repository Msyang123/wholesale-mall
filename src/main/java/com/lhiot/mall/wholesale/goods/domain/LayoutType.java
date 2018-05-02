package com.lhiot.mall.wholesale.goods.domain;

public enum LayoutType {
	tile("水平布局"),
	roll("滚动布局");
	
	private final String displayTag;
	
	public String getDisplayTag(){
		return this.displayTag;
	}
	
	LayoutType(String displayTag){
		this.displayTag = displayTag;
	}
}
