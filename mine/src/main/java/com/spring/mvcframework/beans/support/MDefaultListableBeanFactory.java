package com.spring.mvcframework.beans.support;

import com.spring.mvcframework.beans.config.MBeanDefinition;
import com.spring.mvcframework.context.support.MAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created By Rick 2019/4/18
 */
public class MDefaultListableBeanFactory extends MAbstractApplicationContext {

    //存储注册信息的BeanDefinition,伪IOC容器
    protected final Map<String, MBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, MBeanDefinition>();
}
