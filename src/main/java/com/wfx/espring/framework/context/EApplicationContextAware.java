package com.wfx.espring.framework.context;

/**
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.context ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-17-12:57:09
 * @Description: ͨ�����ʽ���  IOC�����Ķ�����ƣ����潫ͨ��һ��������ȥɨ�����е��ֻ࣬Ҫʵ���˴˽ӿ�
 * ���Զ�����  setApplicationContext()�������Ӷ��� IOC����ע�뵽Ŀ������
 **/
public interface EApplicationContextAware {
    void setApplicationContext(EApplicationContext applicationContext);
}
