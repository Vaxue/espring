package com.wfx.espring.framework.aop.support;

import com.wfx.espring.framework.aop.aspect.EAfterReturningAdviceInterceptor;
import com.wfx.espring.framework.aop.aspect.EAfterThrowingAdviceInterceptor;
import com.wfx.espring.framework.aop.aspect.EBeforeMethodAdviceInterceptor;
import com.wfx.espring.framework.aop.config.EAopConfig;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.aop.support 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-20-13:42:10
 * @Description: 描述
 **/
public class EAdviseSupport {
    private Class<?> targetClass;
    private Object target;
    private EAopConfig config;
    private Pattern pointCutClassPattern;
    private transient Map<Method, List<Object>> methodCache;

    public EAdviseSupport(EAopConfig config) {
        this.config = config;
    }

    public Class<?> getTargetClass(){
        return this.targetClass;
    }

    public Object getTarget(){
        return this.target;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception{
        List<Object> cached = methodCache.get(method);
        if (cached == null) {
            Method m = targetClass.getMethod(method.getName());

            cached = methodCache.get(m);

            //底层逻辑，对代理方法进行一个兼容处理
            this.methodCache.put(m,cached);
        }
        return cached;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }

    private void parse() {
        try {
            String pointCut = config.getPointCut()
                    .replaceAll("\\\\.\\*",".*")
                    .replaceAll("\\(","\\\\(")
                    .replaceAll("\\)","\\\\)");
            //pointCut=public .* com.gupaoedu.vip.spring.demo.service..*Service..*(.*)
            //玩正则
            String pointCutForClassRegex = pointCut.substring(0,pointCut.lastIndexOf("\\(") - 3);
            pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(
                    pointCutForClassRegex.lastIndexOf(" ") + 1));

            methodCache = new HashMap<Method, List<Object>>();
            Pattern pattern = Pattern.compile(pointCut);

            Class aspectClass = Class.forName(this.config.getAspectClass());
            Map<String,Method> aspectMethods = new HashMap<>();
            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(), method);
            }

            for (Method method : this.targetClass.getMethods()) {
                String methodString = method.toString();
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }
                Matcher matcher = pattern.matcher(methodString);
                if (matcher.matches()) {
                    //执行器链
                    List<Object> advices = new LinkedList<>();
                    //把每一个方法包装成 MethodInterceptor
                    //before
                    if (!(null == config.getAspectBefore()) || "".equals(config.getAspectBefore())) {
                        //创建一个Advivce
                        advices.add(new EBeforeMethodAdviceInterceptor(aspectMethods.get(config.getAspectBefore()),aspectClass.newInstance()));
                    }
                    //after
                    if (!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))) {
                        //创建一个Advivce
                        advices.add(new EAfterReturningAdviceInterceptor(aspectMethods.get(config.getAspectAfter()), aspectClass.newInstance()));
                    }
                    //afterThrowing
                    if(!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))) {
                        //创建一个Advivce
                        EAfterThrowingAdviceInterceptor throwingAdvice =
                                new EAfterThrowingAdviceInterceptor(
                                        aspectMethods.get(config.getAspectAfterThrow()),
                                        aspectClass.newInstance());
                        throwingAdvice.setThrowingName(config.getAspectAfterThrowingName());
                        advices.add(throwingAdvice);
                    }
                    methodCache.put(method,advices);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
