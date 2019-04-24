package com.spring.mvcframework.beans.config;

/**
 * Created By Rick 2019/4/18
 */
public class MBeanDefinition {
    private String beanClassName;       //比如一个类是com.spring.demo.action.Student.calsss,那么beanClassName=com.spring.demo.action.Student
    private boolean lazyInit = false;
    private String factoryBeanName;     //spring底层的类名，比如一个类是Student.calsss,那么factoryBeanName=Student
    private boolean isSingleton = true;

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public void setSingleton(boolean singleton) {
        isSingleton = singleton;
    }
}
