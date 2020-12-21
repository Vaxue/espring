package com.wfx.espring.framework.aop.intercept;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.aop.intercept 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-20-14:24:37
 * @Description: 描述
 **/
public interface EMethodInterceptor {
    Object invoke(EMethodInvocation invocation) throws Throwable;
}
