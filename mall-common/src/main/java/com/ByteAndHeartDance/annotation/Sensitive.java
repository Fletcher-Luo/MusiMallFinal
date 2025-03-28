package com.ByteAndHeartDance.annotation;


import com.ByteAndHeartDance.enums.SensitiveTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据脱敏注解
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Sensitive {

    SensitiveTypeEnum type() default SensitiveTypeEnum.DEFAULT;
}
