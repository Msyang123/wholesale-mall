package com.lhiot.mall.wholesale.base;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheCollection {
    String[] cacheNames() default {};
    KeyExpress[] key() default {};
}
