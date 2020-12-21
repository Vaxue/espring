package com.wfx.espring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.aop.aspect ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-20-13:55:58
 * @Description: ����
 **/
public abstract class EAbstractAspectAdvice implements EAdvice{
    private Method aspectMethod;
    private Object aspectTarget;

    public EAbstractAspectAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    public Object invokeAdviceMethod(EJoinPoint joinPoint,Object returnValue,Throwable tx) throws Throwable{
        //�Ȼ�ȡ�����Ĳ����������飬=
        Class<?>[] parameterTypes = this.aspectMethod.getParameterTypes();
        //���û�в�����ֱ�ӵ��÷���
        if (null == parameterTypes || parameterTypes.length == 0) {
            return this.aspectMethod.invoke(aspectTarget);
        } else {
            //����в��������������β��ڵ��÷���
            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i] == EJoinPoint.class) {
                    args[i] = joinPoint;
                } else if (parameterTypes[i] == Throwable.class) {
                    args[i] = tx;
                } else if (parameterTypes[i] == Object.class) {
                    args[i] = returnValue;
                }
            }
            return this.aspectMethod.invoke(aspectTarget,args);
        }
    }
}
