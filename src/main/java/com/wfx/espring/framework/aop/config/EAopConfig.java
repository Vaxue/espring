package com.wfx.espring.framework.aop.config;

import lombok.Data;

/**
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.aop.config ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-20-13:53:55
 * @Description: ����
 **/
@Data
public class EAopConfig {
    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectClass;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;
}
