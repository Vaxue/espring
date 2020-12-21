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
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.webmvc.servlet ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-18-14:47:52
 * @Description: ��ͼ��
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
     * ��Ⱦ��ͼ
     * @param model
     * @param request
     * @param response
     */
    public void render(Map<String,?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //����һ��֧�ֲ�����ȫ��StringBuffer �����reponse.write����������
        StringBuffer sb = new StringBuffer();

        //����һ��ֻ�ܶ��ļ�����
        RandomAccessFile ra = new RandomAccessFile(this.viewFile,"r");

        String line = null;
        while (null != (line = ra.readLine())) {
            line  = new String(line.getBytes("iso-8859-1"),"utf-8");//��������
            Pattern pattern = Pattern.compile("��\\{[^\\}]+\\}",Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                String paramName = matcher.group();
                paramName = paramName.replaceAll("��\\{|\\}","");
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

    //���������ַ�
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
