package com.lhiot.mall.wholesale;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "wholesale-mall.rest-template")
public class RestClientAutoConfigure {
	
	private Integer readTimeout = 5000;
	private Integer connectTimeout = 5000;
	private String stringHttpMessageConverterDefaultCharset = "UTF-8";

    @Bean
	public RestTemplate restOperations(){
    	SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    	factory.setReadTimeout(readTimeout);
    	factory.setConnectTimeout(connectTimeout);
        RestTemplate restTemplate = new RestTemplate(factory);
        //加上一个拦截器
        //restTemplate.setInterceptors(Collections.singletonList(new Interceptor()));
        // 默认的 string conver 的编码集为 "ISO-8859-1"， 这里改为utf-8 编码集
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> converter : messageConverters) {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) converter).setDefaultCharset(Charset.forName(stringHttpMessageConverterDefaultCharset));
            }
        }
        return restTemplate;
	}
}
