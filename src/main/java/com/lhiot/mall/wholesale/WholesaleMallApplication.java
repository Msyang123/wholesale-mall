package com.lhiot.mall.wholesale;

import com.leon.microx.util.SnowflakeId;
import com.lhiot.mall.wholesale.base.SpringHolder;
import com.lhiot.mall.wholesale.user.wechat.PaymentProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
public class WholesaleMallApplication {

    @Bean
    public SpringHolder springHolder() {
        return new SpringHolder();
    }

    @Bean
    public SnowflakeId snowflakeId(Environment environment) {
        long workerId = environment.getRequiredProperty("wholesale-mall.id-generator.worker-id", Long.TYPE);
        long dataCenterId = environment.getRequiredProperty("wholesale-mall.id-generator.data-center-id", Long.TYPE);
        return new SnowflakeId(workerId, dataCenterId);
    }

    public static void main(String[] args) {
        SpringApplication.run(WholesaleMallApplication.class, args);
    }
}
