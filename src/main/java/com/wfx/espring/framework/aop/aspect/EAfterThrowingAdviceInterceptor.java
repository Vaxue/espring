package com.wfx.espring.framework.aop.aspect;

import com.wfx.espring.framework.aop.intercept.EMethodInterceptor;
import com.wfx.espring.framework.aop.intercept.EMethodInvocation;

import java.lang.reflect.Method;

/**
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.aop.aspect ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-20-13:59:19
 * @Description: ����
 **/
public class EAfterThrowingAdviceInterceptor extends EAbstractAspectAdvice implements EAdvice, EMethodInterceptor {

    private String throwingName;

    public void setThrowingName(String throwingName) {
        this.throwingName = throwingName;
    }

    public EAfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(EMethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        }catch (Throwable e){
            invokeAdviceMethod(invocation,null,e.getCause());
            throw e;
        }
    }
}
