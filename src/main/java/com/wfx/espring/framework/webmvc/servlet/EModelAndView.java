package com.wfx.espring.framework.webmvc.servlet;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.webmvc.servlet 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-18-14:11:38
 * @Description: 保存视图名称和实体的类
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
