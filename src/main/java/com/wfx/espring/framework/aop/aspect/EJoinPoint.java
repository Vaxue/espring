package com.wfx.espring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.aop.aspect ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-20-13:57:26
 * @Description: ����
 **/
public interface EJoinPoint {
    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key,Object value);

    Object getUserAttribute(String key);
}
