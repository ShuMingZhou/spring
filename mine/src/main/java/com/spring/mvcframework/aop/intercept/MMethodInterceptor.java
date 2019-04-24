package com.spring.mvcframework.aop.intercept;

/**
 * Created By Rick 2019/4/22
 */
public interface MMethodInterceptor {
    Object invoke(MMethodInvocation invocation) throws Throwable;
}
