package com.spring.mvcframework.aop.aspect;

import com.spring.mvcframework.aop.intercept.MMethodInterceptor;
import com.spring.mvcframework.aop.intercept.MMethodInvocation;

import java.lang.reflect.Method;

public class MAfterReturningAdviceInterceptor extends MAbstractAspectAdvice implements MAdvice, MMethodInterceptor {

    private MJoinPoint joinPoint;

    public MAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MMethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.joinPoint = mi;
        this.afterReturning(retVal,mi.getMethod(),mi.getArguments(),mi.getThis());
        return retVal;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint,retVal,null);
    }
}
