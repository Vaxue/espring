package com.wfx.espring.framework.aop;

import com.wfx.espring.framework.aop.support.EAdviseSupport;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.aop 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-20-13:34:53
 * @Description: 描述
 **/
public class ECgligAopProxy implements EAopProxy{
    private EAdviseSupport advised;

    public ECgligAopProxy(EAdviseSupport advised) {
        this.advised = advised;
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
