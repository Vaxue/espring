package com.wfx.espring.framework.aop.aspect;

import com.sun.scenario.effect.impl.prism.PrTexture;
import com.wfx.espring.framework.aop.intercept.EMethodInterceptor;
import com.wfx.espring.framework.aop.intercept.EMethodInvocation;

import java.lang.reflect.Method;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.aop.aspect 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-20-13:59:51
 * @Description: 描述
 **/
public class EBeforeMethodAdviceInterceptor extends EAbstractAspectAdvice implements EAdvice, EMethodInterceptor {
    private EJoinPoint joinPoint;
    public EBeforeMethodAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void before(Method method, Object[] args,Object target) throws Throwable{
        //传送给了织入参数
        super.invokeAdviceMethod(this.joinPoint,null,null);
    }

    @Override
    public Object invoke(EMethodInvocation invocation) throws Throwable {
        //从被织入的代码中才能拿到，JoinPoint
        this.joinPoint = invocation;
        before(invocation.getMethod(),invocation.getArguments(),invocation.getThis());
        return invocation.proceed();
    }
}
