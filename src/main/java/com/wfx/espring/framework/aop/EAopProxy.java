package com.wfx.espring.framework.aop;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.aop 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-20-13:25:27
 * @Description: Aop顶层设计
 **/
public interface EAopProxy {
    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
