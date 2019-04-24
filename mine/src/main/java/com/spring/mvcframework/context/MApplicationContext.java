package com.spring.mvcframework.context;

import com.spring.mvcframework.annotation.MAutowired;
import com.spring.mvcframework.annotation.MController;
import com.spring.mvcframework.annotation.MService;
import com.spring.mvcframework.aop.MAopProxy;
import com.spring.mvcframework.aop.MCglibAopProxy;
import com.spring.mvcframework.aop.config.MAopConfig;
import com.spring.mvcframework.aop.support.MAdvisedSupport;
import com.spring.mvcframework.aop.support.MJdkDynamicAopProxy;
import com.spring.mvcframework.beans.MBeanWrapper;
import com.spring.mvcframework.beans.config.MBeanDefinition;
import com.spring.mvcframework.beans.config.MBeanPostProcessor;
import com.spring.mvcframework.beans.support.MBeanDefinitionReader;
import com.spring.mvcframework.beans.support.MDefaultListableBeanFactory;
import com.spring.mvcframework.core.MBeanFactory;
import lombok.Data;
import sun.net.ftp.FtpClient;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created By Rick 2019/4/18
 */
@Data
public class MApplicationContext extends MDefaultListableBeanFactory implements MBeanFactory {
    private String[] configLocations;
    private MBeanDefinitionReader reader;

    //单例的IOC容器缓存
    private Map<String,Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>();
    //通用的IOC容器
    private Map<String, MBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, MBeanWrapper>();

    public MApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            refresh();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        //1、定位，定位配置文件
        reader = new MBeanDefinitionReader(configLocations);

        //2、加载配置文件，扫描相关的类，把它们封装成BeanDefinition
        List<MBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        //3、注册，把配置信息放到容器里面(伪IOC容器)
        doRegisterBeanDefinition(beanDefinitions);

        //4、把不是延时加载的类，有提前初始化
        doAutowrited();
    }

    //只处理非延时加载的情况
    private void doAutowrited() {
        for (Map.Entry<String,MBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //初始化IOC容器
    private void doRegisterBeanDefinition(List<MBeanDefinition> beanDefinitions) throws Exception {
        for (MBeanDefinition beanDefinition : beanDefinitions) {
            if(super.beanDefinitionMap.containsKey(beanDefinition)){
                throw new Exception("The “" + beanDefinition.getFactoryBeanName() + "” is exists!!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }
    }
    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    //依赖注入，从这里开始，通过读取BeanDefinition中的信息
    //然后，通过反射机制创建一个实例并返回
    //Spring做法是，不会把最原始的对象放出去，会用一个BeanWrapper来进行一次包装
    //装饰器模式：
    //1、保留原来的OOP关系
    //2、我需要对它进行扩展，增强（为了以后AOP打基础）
    @Override
    public Object getBean(String beanName) throws Exception {
        MBeanDefinition mBeanDefinition = this.beanDefinitionMap.get(beanName);
        Object instance = null;

        //这个逻辑还不严谨，自己可以去参考Spring源码
        //工厂模式 + 策略模式
        MBeanPostProcessor postProcessor = new MBeanPostProcessor();

        postProcessor.postProcessBeforeInitialization(instance,beanName);

        instance = instantiateBean(beanName,mBeanDefinition);

        //3、把这个对象封装到BeanWrapper中
        MBeanWrapper beanWrapper = new MBeanWrapper(instance);
        //4、把BeanWrapper存到IOC容器里面
//        //1、初始化

//        //class A{ B b;}
//        //class B{ A a;}
//        //先有鸡还是先有蛋的问题，一个方法是搞不定的，要分两次

        //2、拿到BeanWraoper之后，把BeanWrapper保存到IOC容器中去
        this.factoryBeanInstanceCache.put(beanName,beanWrapper);

        postProcessor.postProcessAfterInitialization(instance,beanName);

        //3、注入
        populateBean(beanName,new MBeanDefinition(),beanWrapper);

        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    private void populateBean(String beanName, MBeanDefinition mBeanDefinition, MBeanWrapper beanWrapper) {
        Object instance = beanWrapper.getWrappedInstance();
        Class<?> clazz = beanWrapper.getWrappedClass();
        //判断只有加了注解的类，才执行依赖注入
        if(!(clazz.isAnnotationPresent(MController.class) || clazz.isAnnotationPresent(MService.class))){
            return;
        }

        //获得所有的fields
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if(!field.isAnnotationPresent(MAutowired.class)){ continue;}
            MAutowired autowired = field.getAnnotation(MAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }

            //强制访问
            field.setAccessible(true);

            try {
                if(this.factoryBeanInstanceCache.get(autowiredBeanName) == null){ continue; }
                field.set(instance,this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Object instantiateBean(String beanName, MBeanDefinition mBeanDefinition) {
        //1、拿到要实例化的对象的类名
        String className = mBeanDefinition.getBeanClassName();

        //2、反射实例化，得到一个对象
        Object instance = null;
        try {
            if(this.factoryBeanObjectCache.containsKey(className)){
                instance = this.factoryBeanObjectCache.get(className);
            }else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                MAdvisedSupport config = instantionAopConfig(mBeanDefinition);
                config.setTargetClass(clazz);
                config.setTarget(instance);

                //符合PointCut的规则的话，创建代理对象
                if(config.pointCutMatch()) {
                    instance = createProxy(config).getProxy();
                }

                this.factoryBeanObjectCache.put(className,instance);
                this.factoryBeanObjectCache.put(mBeanDefinition.getFactoryBeanName(),instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    private MAopProxy createProxy(MAdvisedSupport config) {

        Class targetClass = config.getTargetClass();
        if(targetClass.getInterfaces().length > 0){
            return new MJdkDynamicAopProxy(config);
        }
        return new MCglibAopProxy(config);
    }

    private MAdvisedSupport instantionAopConfig(MBeanDefinition mBeanDefinition) {
        MAopConfig config = new MAopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new MAdvisedSupport(config);
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public Properties getConfig(){
        return this.reader.getConfig();
    }
}
