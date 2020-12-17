package com.wfx.espring.framework.beans;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.beans 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-14-01:31:58
 * @Description: 描述
 **/


public class EBeanWrapper {
    private Object wrapperInstance;
    private Class<?> wrapperdClass;

    public EBeanWrapper(Object wrapperInstance) {
        this.wrapperInstance = wrapperInstance;
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    //返回代理后的Class
    public Class<?> getWrapperdClass() {
        return wrapperInstance.getClass();
    }
}
