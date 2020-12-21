package com.wfx.espring.framework.aop.aspect;

import com.sun.scenario.effect.impl.prism.PrTexture;
import com.wfx.espring.framework.aop.intercept.EMethodInterceptor;
import com.wfx.espring.framework.aop.intercept.EMethodInvocation;

import java.lang.reflect.Method;

/**
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.aop.aspect ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-20-13:59:51
 * @Description: ����
 **/
public class EBeforeMethodAdviceInterceptor extends EAbstractAspectAdvice implements EAdvice, EMethodInterceptor {
    private EJoinPoint joinPoint;
    public EBeforeMethodAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void before(Method method, Object[] args,Object target) throws Throwable{
        //���͸���֯�����
        super.invokeAdviceMethod(this.joinPoint,null,null);
    }

    @Override
    public Object invoke(EMethodInvocation invocation) throws Throwable {
        //�ӱ�֯��Ĵ����в����õ���JoinPoint
        this.joinPoint = invocation;
        before(invocation.getMethod(),invocation.getArguments(),invocation.getThis());
        return invocation.proceed();
    }
}
