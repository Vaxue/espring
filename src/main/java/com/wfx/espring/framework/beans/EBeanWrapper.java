package com.wfx.espring.framework.beans;

/**
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.beans ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-14-01:31:58
 * @Description: ����
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

    //���ش�����Class
    public Class<?> getWrapperdClass() {
        return wrapperInstance.getClass();
    }
}
