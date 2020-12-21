package com.wfx.espring.framework.beans.config;

/**
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.beans.config ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-17-14:17:58
 * @Description: ����
 **/
public class EBeanPostProcessor {


    //Ϊbean��ʼ��֮ǰ�ṩ���
    public Object postProcessBeforeInitialization(Object bean,String beanName) throws Exception {
        return bean;
    }

    //Ϊbean��ʼ��֮���ṩ���
    public Object postProcessAfterInitialization(Object bean,String beanName) throws Exception {
        return bean;
    }
}
