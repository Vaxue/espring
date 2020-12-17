package com.wfx.espring.framework.beans.config;

import lombok.Data;

/**
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.beans.config ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-14-01:24:26
 * @Description: �����洢�����ļ��е���Ϣ
 **/
@Data
public class EBeanDefinition {
    private String beanClassName;
    private boolean lazyIniti = false;
    private String factoryBeanName;
}
