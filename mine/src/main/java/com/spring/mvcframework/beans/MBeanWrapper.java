package com.spring.mvcframework.beans;

/**
 * Created By Rick 2019/4/18
 */
public class MBeanWrapper {
    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public MBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return this.wrappedInstance;
    }

    // 返回代理以后的Class
    // 可能会是这个 $Proxy0
    public Class<?> getWrappedClass() {
        return this.wrappedInstance.getClass();
    }
}
