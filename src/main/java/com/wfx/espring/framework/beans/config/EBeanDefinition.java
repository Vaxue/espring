package com.wfx.espring.framework.beans.config;

import lombok.Data;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.beans.config 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-14-01:24:26
 * @Description: 用来存储配置文件中的信息
 **/
@Data
public class EBeanDefinition {
    private String beanClassName;
    private boolean lazyIniti = false;
    private String factoryBeanName;
}
