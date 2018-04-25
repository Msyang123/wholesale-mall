package com.lhiot.mall.wholesale.user.wechat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@Data
@ConfigurationProperties(prefix = PaymentProperties.PROPERTIES_PREFIX)
public class PaymentProperties {

    public static final String PROPERTIES_PREFIX = "food-see.payment";
    /**
     * 编码
     */
    private String charset = "UTF-8";
    /**
     * http连接超时（毫秒数）
     */
    private Integer httpConnectionTimeoutExpress = -1;
    /**
     * 临时订单时效毫秒（订单从待支付到支付完成的有效毫秒数，过期则修改订单状态为失效）
     */
    private long temporaryOrderExpirationMs = 30 * 60 * 1000;

    private WeChatOauth weChatOauth;

    @Data
    public static final class WeChatOauth {
        /**
         * 支付超时（分钟。最短失效时间间隔必须大于5分钟）
         */
        private Integer timeoutExpress = 6;
        /**
         * APPID
         */
        private String appId;
        /**
         * APP密钥
         */
        private String appSecret;

        /**
         * authorize回调地址
         */
        private String appRedirectUri;
    }

    private WeChatPayConfig weChatPay;

    @Data
    public static final class WeChatPayConfig {
        /**
         * 支付超时（分钟。最短失效时间间隔必须大于5分钟）
         */
        private Integer timeoutExpress = 6;
        /**
         * 商户号
         */
        private String partnerId;
        /**
         * 商户密钥
         */
        private String partnerKey;
        /**
         * pkcs12证书
         */
        private Resource pkcs12;

        /**
         * 异步支付回调地址
         */
        private String rechargeNotifyUrl;
        /**
         * 异步订单回调地址
         */
        private String orderNotifyUrl;

        /**
         * 支付换算
         * # 1：1倍，单位还是分； 100：100倍，单位就变成元了
         */
        private Integer payunit;

        /**
         * 前端静态页使用代理
         */
        private String proxy;
    }
}
