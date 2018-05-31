package com.lhiot.mall.wholesale;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 当前应用配置类
 * Created by lynn on 5/31
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wholesale-mall.inventory")
public class ApplicationConfiguration {
    private String receiverCode;
    
}
