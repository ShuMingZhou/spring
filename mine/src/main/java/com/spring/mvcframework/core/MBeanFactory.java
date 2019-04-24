package com.spring.mvcframework.core;

/**
 * 单例工厂的顶层设计
 * Created By Rick 2019/4/18
 */
public interface MBeanFactory {
    /**
     * 根据beanName从IOC容器中获得一个实例Bean
     * @param beanName
     * @return
     */
    Object getBean(String beanName) throws Exception;

    Object getBean(Class<?> beanClass) throws Exception;
}
