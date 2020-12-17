package com.wfx.espring.framework.annotation;

import java.lang.annotation.*;

/**
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.annotation ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-14-01:07:59
 * @Description: ����
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EComponent {
    String value() default "";
}
