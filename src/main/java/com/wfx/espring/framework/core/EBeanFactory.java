package com.wfx.espring.framework.core;

/**
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.core ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-14-01:19:04
 * @Description: ���������Ķ������
 **/
public interface EBeanFactory {
    //����beanName��IOC�����л��һ��ʵ��Bean
    Object getBean(String beanName) throws Exception;
    Object getBean(Class<?> beanClass) throws Exception;
}
