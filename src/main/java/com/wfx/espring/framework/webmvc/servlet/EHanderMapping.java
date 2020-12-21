package com.wfx.espring.framework.webmvc.servlet;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.webmvc.servlet ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-18-14:29:44
 * @Description: ����Controller���淽����RequestMapping��url�Ĺ�ϵ
 **/

@Data
@AllArgsConstructor
public class EHanderMapping {
    private Object controller;//���淽����Ӧ��ʵ��
    private Method method; //����ӳ��ķ���
    private Pattern pattern; //����URL������ƥ��
}
