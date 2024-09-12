package com.sky.annotation;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/9/11
 * @version: 1.0
 */

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动填充注解
 */
@Target(ElementType.METHOD)//方法上
@Retention(RetentionPolicy.RUNTIME)//运行时
public @interface AutoFill {
    //数据库操作类型
    OperationType value();
}
