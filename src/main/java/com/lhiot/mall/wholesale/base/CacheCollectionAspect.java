package com.lhiot.mall.wholesale.base;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import java.lang.reflect.Method;

@Slf4j
@Aspect
public class CacheCollectionAspect {

    @Autowired
    private CacheManager cacheManager;

    @Pointcut("@within(org.springframework.stereotype.Service)&&execution(@CacheCollection * *.*(..))")
    public void aspect() {
    }

    @Around("aspect()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        CacheCollection cacheAnnotation = method.getAnnotation(CacheCollection.class);
        // FIXME 自定义缓存处理List
//        Object generateKey = SimpleKeyGenerator.generateKey(cacheAnnotation.key());
//        Arrays.stream(cacheAnnotation.cacheNames()).forEach(cacheName -> {
//            this.cacheManager.getCache(cacheName).get(generateKey);
//        });
        return pjp.proceed();
    }
}