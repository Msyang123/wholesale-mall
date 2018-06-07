package com.lhiot.mall.wholesale.base.duplicateaop;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
@Documented
public @interface DuplicateSubmitToken {
    //保存重复提交标记 默认为需要保存
    boolean save() default true;
}
