package com.wfx.espring.framework.beans.support;

import com.wfx.espring.framework.beans.config.EBeanDefinition;
import com.wfx.espring.framework.context.support.EAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.beans.support 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-15-18:24:35
 * @Description: 描述
 **/
public class EDefaultListableBeanFactory extends EAbstractApplicationContext {
    //存储注册信息的BeanDefinition,伪IOC容器
    protected Map<String, EBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
}
