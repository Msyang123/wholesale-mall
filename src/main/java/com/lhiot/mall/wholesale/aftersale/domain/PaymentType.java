package com.lhiot.mall.wholesale.aftersale.domain;

public enum PaymentType {
	balance("余额支付"),
	wechat("微信"),
	offline("线下支付");
	
	private final String displayTag;
	
	public String getDisplayTag(){
		return this.displayTag;
	}
	
	PaymentType(String displayTag){
		this.displayTag = displayTag;
	}
}
