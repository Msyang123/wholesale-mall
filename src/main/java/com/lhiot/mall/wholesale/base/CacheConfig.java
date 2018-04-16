package com.lhiot.mall.wholesale.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Slf4j
@Configuration
@EnableCaching
@EnableAspectJAutoProxy
public class CacheConfig {

    @Bean
    public CacheCollectionAspect cacheCollectionAspect(){
        return new CacheCollectionAspect();
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(Environment env) {
        Long cacheExpiration = Long.valueOf(env.getProperty("spring.cache.expiration", "3600"));
        return RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
        ).entryTtl(
                Duration.of(cacheExpiration, ChronoUnit.SECONDS)
        );
    }
}
