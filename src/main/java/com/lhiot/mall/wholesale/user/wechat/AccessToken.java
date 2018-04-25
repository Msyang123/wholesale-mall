package com.lhiot.mall.wholesale.user.wechat;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccessToken implements Serializable
{
    private static final long serialVersionUID = -4093601016338089860L;
    private String accessToken;
    private int expiresIn;
    private String refreshToken;
    private String scope;
    private String openId;
    private String openIdAterMd5;
    
}
