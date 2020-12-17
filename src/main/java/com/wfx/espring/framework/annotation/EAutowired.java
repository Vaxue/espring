package com.wfx.espring.framework.annotation;

import java.lang.annotation.*;

/**
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.annotation ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-14-00:57:46
 * @Description: ����
 **/
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EAutowired {
    String value() default "";
}
