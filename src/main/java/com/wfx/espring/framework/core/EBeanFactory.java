package com.wfx.espring.framework.core;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.core 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-14-01:19:04
 * @Description: 单例工厂的顶层设计
 **/
public interface EBeanFactory {
    //根据beanName从IOC容器中获得一个实例Bean
    Object getBean(String beanName) throws Exception;
    Object getBean(Class<?> beanClass) throws Exception;
}
