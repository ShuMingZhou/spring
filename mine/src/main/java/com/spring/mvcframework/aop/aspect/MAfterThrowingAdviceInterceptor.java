package com.spring.mvcframework.aop.aspect;

import com.spring.mvcframework.aop.intercept.MMethodInterceptor;
import com.spring.mvcframework.aop.intercept.MMethodInvocation;

import java.lang.reflect.Method;

public class MAfterThrowingAdviceInterceptor extends MAbstractAspectAdvice implements MAdvice, MMethodInterceptor {


    private String throwingName;

    public MAfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MMethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }catch (Throwable e){
            invokeAdviceMethod(mi,null,e.getCause());
            throw e;
        }
    }

    public void setThrowName(String throwName){
        this.throwingName = throwName;
    }
}
