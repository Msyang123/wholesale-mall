package com.lhiot.mall.wholesale.user.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 微信jsapi调用返回结果
 */
@Data
public class JsapiPaySign {
	 /** 
     * app应用id
     */  
    private String appId;
    /** 
     * 时间（秒）
     */  
    private String timestamp;

    @JsonProperty("nonce_str")
    private String nonceStr;

    private String signature;
}
