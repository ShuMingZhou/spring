package com.spring.mvcframework.beans.config;

/**
 * Created By Rick 2019/4/18
 */
public class MBeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }
}
