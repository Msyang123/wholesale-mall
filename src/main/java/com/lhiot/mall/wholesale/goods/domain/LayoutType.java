package com.lhiot.mall.wholesale.goods.domain;

public enum LayoutType {
	tilesLayout("水平布局"),
	rollingLayout("滚动布局");
	
	private final String displayTag;
	
	public String getDisplayTag(){
		return this.displayTag;
	}
	
	LayoutType(String displayTag){
		this.displayTag = displayTag;
	}
}
