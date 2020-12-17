package com.wfx.espring.framework.beans.support;

import com.wfx.espring.framework.beans.config.EBeanDefinition;
import com.wfx.espring.framework.context.support.EAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.beans.support ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-15-18:24:35
 * @Description: ����
 **/
public class EDefaultListableBeanFactory extends EAbstractApplicationContext {
    //�洢ע����Ϣ��BeanDefinition,αIOC����
    protected Map<String, EBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
}
