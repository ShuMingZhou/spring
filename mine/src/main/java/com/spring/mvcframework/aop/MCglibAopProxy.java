package com.spring.mvcframework.aop;

import com.spring.mvcframework.aop.support.MAdvisedSupport;

/**
 * Created By Rick 2019/4/22
 */
public class MCglibAopProxy implements MAopProxy {

    public MCglibAopProxy(MAdvisedSupport config) { }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
