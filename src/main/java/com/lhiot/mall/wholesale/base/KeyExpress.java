package com.lhiot.mall.wholesale.base;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface KeyExpress {
    String prefix() default "";
    String express() default "";
}
