package com.wfx.espring.framework.webmvc.servlet;

import java.io.File;
import java.util.Locale;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.webmvc.servlet 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-18-15:59:03
 * @Description: 描述
 **/
public class EViewResolver {

    private final String DEFAULT_TEMPLATE_SUFFX = ".html";

    private File templateRootDir;

    public EViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templateRootPath);
    }

    public EView resolveViewName(String viewName, Locale locale) {
        if (null == viewName || "".equals(viewName.trim())) {return null;}
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFX) ? viewName : viewName + DEFAULT_TEMPLATE_SUFFX;
        File templateFile = new File((templateRootDir.getPath()+"/"+viewName).replaceAll("/+","/"));
        return new EView(templateFile);
    }
}
