package com.spring.mvcframework.aop;

/**
 * Created By Rick 2019/4/22
 */
public interface MAopProxy {
    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
