package com.wfx.espring.framework.aop.aspect;

import com.wfx.espring.framework.aop.intercept.EMethodInterceptor;
import com.wfx.espring.framework.aop.intercept.EMethodInvocation;

import java.lang.reflect.Method;

/**
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.aop.aspect ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-20-13:58:55
 * @Description: ����
 **/
public class EAfterReturningAdviceInterceptor extends EAbstractAspectAdvice implements EAdvice,EMethodInterceptor {

    private EJoinPoint joinPoint;


    public EAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(EMethodInvocation invocation) throws Throwable {
        Object retVal = invocation.proceed();
        this.joinPoint = invocation;
        this.afterReturning(retVal,invocation.getMethod(),invocation.getArguments(),invocation.getThis());
        return retVal;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint,retVal,null);
    }
}
