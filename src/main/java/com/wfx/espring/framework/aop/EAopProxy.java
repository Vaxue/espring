package com.wfx.espring.framework.aop;

/**
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.aop ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-20-13:25:27
 * @Description: Aop�������
 **/
public interface EAopProxy {
    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
