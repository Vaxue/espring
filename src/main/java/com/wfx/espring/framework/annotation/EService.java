package com.wfx.espring.framework.annotation;

import java.lang.annotation.*;

/**
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.annotation ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-14-01:06:20
 * @Description: ����
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EService {
    String value() default "";
}
