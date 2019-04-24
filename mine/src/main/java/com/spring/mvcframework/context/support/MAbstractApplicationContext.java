package com.spring.mvcframework.context.support;

/**
 * IOC容器实现的顶层设计
 * Created By Rick 2019/4/18
 */
public abstract class MAbstractApplicationContext {
    //受保护，只提供给子类重写
    public void refresh() throws Exception {}
}
