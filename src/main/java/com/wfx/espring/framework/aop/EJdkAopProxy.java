package com.wfx.espring.framework.aop;

import com.wfx.espring.framework.aop.intercept.EMethodInvocation;
import com.wfx.espring.framework.aop.support.EAdviseSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.aop 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-20-13:35:42
 * @Description: 描述
 **/
public class EJdkAopProxy implements EAopProxy, InvocationHandler {
    private EAdviseSupport advised;

    public EJdkAopProxy(EAdviseSupport advised) {
        this.advised = advised;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,this.advised.getTargetClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> interceptorsAndDynamicMethodMatchers = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method,this.advised.getTargetClass());
        EMethodInvocation invocation = new EMethodInvocation(proxy,this.advised.getTarget(),method, args,this.advised.getTargetClass(),interceptorsAndDynamicMethodMatchers);
        return invocation.proceed();
    }
}
