package com.wfx.espring.framework.aop.config;

import lombok.Data;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.aop.config 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-20-13:53:55
 * @Description: 描述
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
