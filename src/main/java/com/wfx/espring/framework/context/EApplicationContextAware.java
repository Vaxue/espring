package com.wfx.espring.framework.context;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.context 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-17-12:57:09
 * @Description: 通过解耦方式获得  IOC容器的顶层设计，后面将通过一个监听器去扫描所有的类，只要实现了此接口
 * 将自动调用  setApplicationContext()方法，从而将 IOC容器注入到目标类中
 **/
public interface EApplicationContextAware {
    void setApplicationContext(EApplicationContext applicationContext);
}
