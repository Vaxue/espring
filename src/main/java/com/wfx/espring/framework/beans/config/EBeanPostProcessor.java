package com.wfx.espring.framework.beans.config;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.beans.config 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-17-14:17:58
 * @Description: 描述
 **/
public class EBeanPostProcessor {


    //为bean初始化之前提供入口
    public Object postProcessBeforeInitialization(Object bean,String beanName) throws Exception {
        return bean;
    }

    //为bean初始化之后提供入口
    public Object postProcessAfterInitialization(Object bean,String beanName) throws Exception {
        return bean;
    }
}
