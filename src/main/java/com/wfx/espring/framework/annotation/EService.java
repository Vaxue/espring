package com.wfx.espring.framework.annotation;

import java.lang.annotation.*;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.annotation 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-14-01:06:20
 * @Description: 描述
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EService {
    String value() default "";
}
