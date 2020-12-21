package com.wfx.espring.framework.webmvc.servlet;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.webmvc.servlet ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-18-14:11:38
 * @Description: ������ͼ���ƺ�ʵ�����
 **/
@Getter
public class EModelAndView {
    private String viewName;
    private Map<String,?> model;

    public EModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public EModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }
}
