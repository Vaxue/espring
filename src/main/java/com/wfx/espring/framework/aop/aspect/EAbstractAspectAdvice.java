package com.wfx.espring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.aop.aspect 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-20-13:55:58
 * @Description: 描述
 **/
public abstract class EAbstractAspectAdvice implements EAdvice{
    private Method aspectMethod;
    private Object aspectTarget;

    public EAbstractAspectAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    public Object invokeAdviceMethod(EJoinPoint joinPoint,Object returnValue,Throwable tx) throws Throwable{
        //先获取方法的参数类型数组，=
        Class<?>[] parameterTypes = this.aspectMethod.getParameterTypes();
        //如果没有参数则直接调用方法
        if (null == parameterTypes || parameterTypes.length == 0) {
            return this.aspectMethod.invoke(aspectTarget);
        } else {
            //如果有参数将参数赋给形参在调用方法
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
