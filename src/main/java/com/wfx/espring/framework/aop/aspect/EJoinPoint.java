package com.wfx.espring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.aop.aspect 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-20-13:57:26
 * @Description: 描述
 **/
public interface EJoinPoint {
    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key,Object value);

    Object getUserAttribute(String key);
}
