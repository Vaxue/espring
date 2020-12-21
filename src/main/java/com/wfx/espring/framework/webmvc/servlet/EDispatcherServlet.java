package com.wfx.espring.framework.webmvc.servlet;

import com.wfx.espring.framework.annotation.EContoller;
import com.wfx.espring.framework.annotation.ERequestMapping;
import com.wfx.espring.framework.context.EApplicationContext;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.webmvc.servlet ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-14-00:50:03
 * @Description: ����
 **/
public class EDispatcherServlet extends HttpServlet {

    /**
     * ����������·��
     */
    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    /**
     * ������
     */
    private EApplicationContext applicationContext;

    /**
     * EHanderMapping�б�
     */
    private List<EHanderMapping> handerMappings = new ArrayList<>();

    /**
     * �洢EHanderMapping��EHandlerAdapter��ӳ���ϵ
     */
    private Map<EHanderMapping,EHandlerAdapter> handerAdapters = new HashMap<>();

    /**
     *EViewResolver�б�
     */
    private List<EViewResolver> viewResolvers = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.doDispatch(req,resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception,Details:\r\n" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", "").replaceAll(",\\s", "\r\n"));
            e.printStackTrace();
        }
    }

    /**
     * ����ת������
     * @param req
     * @param resp
     */
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //1 ��request���õ�һ��url��ȥƥ��һ��HandlerMapping
        EHanderMapping handler = getHandler(req);
        //2 ׼������ǰ�Ĳ���
        EHandlerAdapter handlerAdapter = getHandlerAdapter(handler);
        //3 ��ʼ���� ����ModelAndView�洢��Ҫ����ҳ���ϵ�ֵ��ҳ������
        EModelAndView modelAndView = handlerAdapter.handle(req,resp,handler);
        //4 �������ʾ�������
        processDispatchResult(req,resp,modelAndView);
    }

    /**
     * ��request�л�ȡurl��ƥ��HandlerMapping����
     * @param req
     * @return
     */
    private EHanderMapping getHandler(HttpServletRequest req) {
        if (this.handerMappings.isEmpty()) {return null;}

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath,"").replaceAll("/+","/");

        for (EHanderMapping hander : handerMappings) {
            Matcher matcher = hander.getPattern().matcher(url);
            //���rqu��url�͸�handler��url��ƥ���������һ��
            if (!matcher.matches()) {continue;}
            return hander;
        }
        return null;
    }

    /**
     * ���EHandlerAdapter
     * @param handler
     * @return
     */
    private EHandlerAdapter getHandlerAdapter(EHanderMapping handler) {
        if (this.handerAdapters.isEmpty()) {return null;}
        EHandlerAdapter handlerAdapter = this.handerAdapters.get(handler);

        //�ж�handler�Ƿ���EHanderMapping���ͻ���������
        if (handlerAdapter.suppports(handler)) {
            return handlerAdapter;
        } else {
            return  null;
        }
    }

    /**
     *��EModelAndView������ͼ���
     * @param req
     * @param resp
     * @param modelAndView
     */
    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, EModelAndView modelAndView) throws Exception {
        //��EModelAndView���һ��HTML��OuputStream��json��freemark��veolcity
        if(modelAndView == null) {return;}

        //���viewResolversû��viewResolver����
        if (viewResolvers.isEmpty()) {return;}

        //����viewResolvers
        for (EViewResolver viewResolver : viewResolvers) {
            //Ϊÿһ��viewResolver��ͼ����,����һ��view
            EView view = viewResolver.resolveViewName(modelAndView.getViewName(), null);
            //��Ⱦ��ͼ
            view.render(modelAndView.getModel(),req,resp);
            return;
        }
    }

    /**
     * ��ʼ������
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        //1 ��ʼ��ApplicationContext
        applicationContext = new EApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        //2 ��ʼ��Spring MVC �Ŵ����
        initStrategies(applicationContext);
    }

    private void initStrategies(EApplicationContext context) {
        //���ļ��ϴ������
        initMultipartResolver(context);
        //��ʼ���������Ի���
        initLocaleResolver(context);
        //��ʼ��ģ�崦����
        initThemeResolver(context);


        //handlerMapping������ʵ��
        initHandlerMappings(context);
        //��ʼ������������������ʵ��
        initHandlerAdapters(context);
        //��ʼ���쳣������
        initHandlerExceptionResolvers(context);
        //��ʼ����ͼԤ������
        initRequestToViewNameTranslator(context);


        //��ʼ����ͼת����������ʵ��
        initViewResolvers(context);
        //����������
        initFlashMapManager(context);
    }

    private void initFlashMapManager(EApplicationContext context) {
    }

    private void initViewResolvers(EApplicationContext context) {
        //�õ�ģ��Ĵ��Ŀ¼
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);
        String[] templates = templateRootDir.list();
        for (int i = 0; i < templates.length; i++) {
            //������Ҫ��Ϊ�˼��ݶ�ģ�壬����ģ��Spring��List����
            //����д�Ĵ����м��ˣ���ʵֻ����Ҫһ��ģ��Ϳ��Ը㶨
            //ֻ��Ϊ�˷��棬���л��Ǹ��˸�List
            this.viewResolvers.add(new EViewResolver(templateRoot));
        }

    }

    private void initRequestToViewNameTranslator(EApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(EApplicationContext context) {
    }

    private void initHandlerAdapters(EApplicationContext context) {
        //��һ��requet������һ��handler�����������ַ����ģ��Զ��䵽handler�е��β�
        //�����֪����Ҫ�õ�HandlerMapping���ܸɻ�
        //����ζ�ţ��м���HandlerMapping���м���HandlerAdapter
        for (EHanderMapping handerMapping : this.handerMappings) {
            this.handerAdapters.put(handerMapping,new EHandlerAdapter());
        }
    }

    private void initHandlerMappings(EApplicationContext context) {
        //��ȡ����bean������
        String[] beanNames = context.getBeanDefitioNames();

        try {
            //����beanNames�ҳ����ϴ���EContoller����
            for (String beanName : beanNames) {
                //����beanName��ȡ����
                Object controller = context.getBean(beanName);
                //�����ȡ���class
                Class<?> clazz = controller.getClass();
                //���������û��EContoller.class�������һ��ѭ��
                if (!clazz.isAnnotationPresent(EContoller.class)){continue;}

                String baseUrl = "";
                //��ȡController��url����
                if (clazz.isAnnotationPresent(ERequestMapping.class)) {
                    ERequestMapping requestMapping = clazz.getAnnotation(ERequestMapping.class);
                    baseUrl = requestMapping.value();
                }
                //��ȡMethod��URL��λ��
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    //û�м�RequestMappingע���ֱ�Ӻ���
                    if (!method.isAnnotationPresent(ERequestMapping.class)) {continue;}
                    //ӳ��URL
                    ERequestMapping requestMapping = method.getAnnotation(ERequestMapping.class);
                    String regex = ("/"+baseUrl+"/"+requestMapping.value().replaceAll("\\*",".*")).replaceAll("/+","/");
                    Pattern pattern = Pattern.compile(regex);

                    //��������װ��EHanderMapping�Ž�handerMappings��
                    this.handerMappings.add(new EHanderMapping(controller,method,pattern));
                    //log.info("Mapped " + regex + "," + method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initThemeResolver(EApplicationContext context) {
    }

    private void initLocaleResolver(EApplicationContext context) {
    }

    private void initMultipartResolver(EApplicationContext context) {
    }
}
