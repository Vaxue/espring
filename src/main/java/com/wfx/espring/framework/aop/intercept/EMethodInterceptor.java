package com.wfx.espring.framework.aop.intercept;

/**
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.aop.intercept ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-20-14:24:37
 * @Description: ����
 **/
public interface EMethodInterceptor {
    Object invoke(EMethodInvocation invocation) throws Throwable;
}
