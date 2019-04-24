package com.spring.mvcframework.aop.aspect;

import java.lang.reflect.Method;

/**
 *
 */
public interface MJoinPoint {

    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);
}
