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
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.webmvc.servlet 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-14-00:50:03
 * @Description: 描述
 **/
public class EDispatcherServlet extends HttpServlet {

    /**
     * 上下文配置路径
     */
    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    /**
     * 上下文
     */
    private EApplicationContext applicationContext;

    /**
     * EHanderMapping列表
     */
    private List<EHanderMapping> handerMappings = new ArrayList<>();

    /**
     * 存储EHanderMapping和EHandlerAdapter的映射关系
     */
    private Map<EHanderMapping,EHandlerAdapter> handerAdapters = new HashMap<>();

    /**
     *EViewResolver列表
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
     * 请求转发方法
     * @param req
     * @param resp
     */
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //1 从request中拿到一个url，去匹配一个HandlerMapping
        EHanderMapping handler = getHandler(req);
        //2 准备调用前的参数
        EHandlerAdapter handlerAdapter = getHandlerAdapter(handler);
        //3 开始调用 返回ModelAndView存储了要传到页面上的值和页面名称
        EModelAndView modelAndView = handlerAdapter.handle(req,resp,handler);
        //4 将结果显示到浏览器
        processDispatchResult(req,resp,modelAndView);
    }

    /**
     * 从request中获取url，匹配HandlerMapping方法
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
            //如果rqu的url和该handler的url不匹配则继续下一个
            if (!matcher.matches()) {continue;}
            return hander;
        }
        return null;
    }

    /**
     * 获得EHandlerAdapter
     * @param handler
     * @return
     */
    private EHandlerAdapter getHandlerAdapter(EHanderMapping handler) {
        if (this.handerAdapters.isEmpty()) {return null;}
        EHandlerAdapter handlerAdapter = this.handerAdapters.get(handler);

        //判断handler是否是EHanderMapping类型或它的子类
        if (handlerAdapter.suppports(handler)) {
            return handlerAdapter;
        } else {
            return  null;
        }
    }

    /**
     *将EModelAndView化成视图输出
     * @param req
     * @param resp
     * @param modelAndView
     */
    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, EModelAndView modelAndView) throws Exception {
        //把EModelAndView变成一个HTML、OuputStream、json、freemark、veolcity
        if(modelAndView == null) {return;}

        //如果viewResolvers没有viewResolver返回
        if (viewResolvers.isEmpty()) {return;}

        //遍历viewResolvers
        for (EViewResolver viewResolver : viewResolvers) {
            //为每一个viewResolver视图命名,返回一个view
            EView view = viewResolver.resolveViewName(modelAndView.getViewName(), null);
            //渲染视图
            view.render(modelAndView.getModel(),req,resp);
            return;
        }
    }

    /**
     * 初始化方法
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        //1 初始化ApplicationContext
        applicationContext = new EApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        //2 初始化Spring MVC 九大组件
        initStrategies(applicationContext);
    }

    private void initStrategies(EApplicationContext context) {
        //多文件上传的组件
        initMultipartResolver(context);
        //初始化本地语言环境
        initLocaleResolver(context);
        //初始化模板处理器
        initThemeResolver(context);


        //handlerMapping，必须实现
        initHandlerMappings(context);
        //初始化参数适配器，必须实现
        initHandlerAdapters(context);
        //初始化异常拦截器
        initHandlerExceptionResolvers(context);
        //初始化视图预处理器
        initRequestToViewNameTranslator(context);


        //初始化视图转换器，必须实现
        initViewResolvers(context);
        //参数缓存器
        initFlashMapManager(context);
    }

    private void initFlashMapManager(EApplicationContext context) {
    }

    private void initViewResolvers(EApplicationContext context) {
        //拿到模板的存放目录
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);
        String[] templates = templateRootDir.list();
        for (int i = 0; i < templates.length; i++) {
            //这里主要是为了兼容多模板，所有模仿Spring用List保存
            //在我写的代码中简化了，其实只有需要一个模板就可以搞定
            //只是为了仿真，所有还是搞了个List
            this.viewResolvers.add(new EViewResolver(templateRoot));
        }

    }

    private void initRequestToViewNameTranslator(EApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(EApplicationContext context) {
    }

    private void initHandlerAdapters(EApplicationContext context) {
        //把一个requet请求变成一个handler，参数都是字符串的，自动配到handler中的形参
        //可想而知，他要拿到HandlerMapping才能干活
        //就意味着，有几个HandlerMapping就有几个HandlerAdapter
        for (EHanderMapping handerMapping : this.handerMappings) {
            this.handerAdapters.put(handerMapping,new EHandlerAdapter());
        }
    }

    private void initHandlerMappings(EApplicationContext context) {
        //获取所有bean的名字
        String[] beanNames = context.getBeanDefitioNames();

        try {
            //遍历beanNames找出类上带有EContoller的类
            for (String beanName : beanNames) {
                //根据beanName获取对象
                Object controller = context.getBean(beanName);
                //反射获取类的class
                Class<?> clazz = controller.getClass();
                //如果该类上没有EContoller.class则继续下一轮循环
                if (!clazz.isAnnotationPresent(EContoller.class)){continue;}

                String baseUrl = "";
                //获取Controller的url配置
                if (clazz.isAnnotationPresent(ERequestMapping.class)) {
                    ERequestMapping requestMapping = clazz.getAnnotation(ERequestMapping.class);
                    baseUrl = requestMapping.value();
                }
                //获取Method的URL的位置
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    //没有加RequestMapping注解的直接忽略
                    if (!method.isAnnotationPresent(ERequestMapping.class)) {continue;}
                    //映射URL
                    ERequestMapping requestMapping = method.getAnnotation(ERequestMapping.class);
                    String regex = ("/"+baseUrl+"/"+requestMapping.value().replaceAll("\\*",".*")).replaceAll("/+","/");
                    Pattern pattern = Pattern.compile(regex);

                    //将参数封装成EHanderMapping放进handerMappings中
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
