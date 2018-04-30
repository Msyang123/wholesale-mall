package com.lhiot.mall.wholesale.goods.domain;

public enum KeywordsType {
	goods("商品"),
	category("品类");
	
	private final String displayTag;
	
	public String getDisplayTag(){
		return this.displayTag;
	}
	
	KeywordsType(String displayTag){
		this.displayTag = displayTag;
	}
}
