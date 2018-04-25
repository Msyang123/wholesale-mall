package com.lhiot.mall.wholesale.user.wechat;

import lombok.Data;

@Data
public class JsapiTicket {
	 /** 
     * 有效时长 
     */  
    private int expiresIn;  
    /** 
     * js调用票据 
     */  
    private String ticket;
}
