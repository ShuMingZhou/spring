package com.spring.mvcframework.aop.aspect;

import com.spring.mvcframework.aop.intercept.MMethodInterceptor;
import com.spring.mvcframework.aop.intercept.MMethodInvocation;

import java.lang.reflect.Method;

public class MMethodBeforeAdviceInterceptor extends MAbstractAspectAdvice implements MAdvice, MMethodInterceptor {

    private MJoinPoint joinPoint;
    public MMethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void before(Method method,Object[] args,Object target) throws Throwable{
        //传送了给织入参数
        //method.invoke(target);
        super.invokeAdviceMethod(this.joinPoint,null,null);

    }
    @Override
    public Object invoke(MMethodInvocation mi) throws Throwable {
        //从被织入的代码中才能拿到，JoinPoint
        this.joinPoint = mi;
        before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}
