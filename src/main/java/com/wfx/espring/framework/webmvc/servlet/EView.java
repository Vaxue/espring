package com.wfx.espring.framework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.webmvc.servlet 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-18-14:47:52
 * @Description: 视图类
 **/
public class EView {

    private final String DEFULAT_CONTENT_TYPE = "text/html;charset=utf-8";

    private File viewFile;

    public EView() {
    }
    public EView(File viewFile) {
        this.viewFile = viewFile;
    }

    /**
     * 渲染视图
     * @param model
     * @param request
     * @param response
     */
    public void render(Map<String,?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //创建一个支持并发安全的StringBuffer 最后用reponse.write输出到浏览器
        StringBuffer sb = new StringBuffer();

        //定义一个只能读文件的流
        RandomAccessFile ra = new RandomAccessFile(this.viewFile,"r");

        String line = null;
        while (null != (line = ra.readLine())) {
            line  = new String(line.getBytes("iso-8859-1"),"utf-8");//避免乱码
            Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}",Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                String paramName = matcher.group();
                paramName = paramName.replaceAll("￥\\{|\\}","");
                Object paramValue = model.get(paramName);
                if (null == paramValue) {continue;}
                line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                matcher = pattern.matcher(line);
            }
            sb.append(line);
        }

        response.setCharacterEncoding("utf-8");
        response.setContentType(DEFULAT_CONTENT_TYPE);
        response.getWriter().write(sb.toString());
    }

    //处理特殊字符
    public static String makeStringForRegExp(String str) {
        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }
}
