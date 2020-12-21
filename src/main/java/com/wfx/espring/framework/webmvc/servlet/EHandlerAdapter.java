package com.wfx.espring.framework.webmvc.servlet;

import com.wfx.espring.framework.annotation.ERequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.webmvc.servlet 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-18-18:18:09
 * @Description: 描述
 **/
public class EHandlerAdapter {
    /**
     * 判断EHandlerAdapter是否支持处理Object
     * @param handler
     * @return
     */
    public boolean suppports(Object handler) {return (handler instanceof EHanderMapping);}

    EModelAndView handle(HttpServletRequest request, HttpServletResponse response,Object handler) throws Exception {
        EHanderMapping handlerMapping  = (EHanderMapping) handler;

        //把方法的形参列表和request的参数一一对应
        Map<String,Integer> paramIndexingMapping = new HashMap<>();

        /**
         * 提取方法中加了注解的参数
         * 把方法上的注解拿到，得到的是一个二维数组
         * 因为一个方法有多个参数，一个参数又可以有多个注解
         */
        Annotation[][] params = handlerMapping.getMethod().getParameterAnnotations();
        //遍历方法的每个参数
        for (int i = 0; i < params.length; i++) {
            //遍历参数的注解，然后判断该注解是否是ERequestMapping
            for (Annotation annotation : params[i]) {
                //判断该注解是否是ERequestMapping
                if (annotation.annotationType().equals(ERequestParam.class)) {
                    //如果说是ERequestMapping 则将该参数的名称和在request参数列表的位置i
                    // 添加进paramIndexingMapping中去
                    String paramName = ((ERequestParam)annotation).value();
                    if (!"".equals(paramName.trim())) {
                        paramIndexingMapping.put(paramName,i);
                    }
                }
            }
        }

        //提取方法中的request和response参数
        Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();
        //遍历parameterTypes
        for (int i = 0; i < paramTypes.length; i++) {
            //如果参数是HttpServletRequest类型或者HttpServletResponse类型
            //则将他们的名称和对应的位置i加入paramIndexingMapping中
            Class<?> type = paramTypes[i];
            if (type ==  HttpServletRequest.class || type == HttpServletResponse.class) {
                paramIndexingMapping.put(type.getName(), i);
            }
        }

        //获得方法的形参列表
        Map<String,String[]> paramMap = request.getParameterMap();

        //实参列表
        Object[] paramValues = new Object[paramTypes.length];

        //将paramIndexingMapping中的形参的参数值放入实参列表中
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            String value = Arrays.toString(entry.getValue()).replaceAll("\\[","")
                    .replaceAll("\\]","")
                    .replaceAll("\\s",",");

            //如果参数不在paramIndexingMapping中跳过
            if (!paramIndexingMapping.containsKey(entry.getKey())) {continue;}

            //否则将参数添加进实参列表中
            int index = paramIndexingMapping.get(entry.getKey());
            paramValues[index] = caseStringValue(value,paramTypes[index]);
        }

        //将方法中的request参数添加进实参列表中
        if (paramIndexingMapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = paramIndexingMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = request;
        }

        //将方法中的response参数添加实参列表中
        if (paramIndexingMapping.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = paramIndexingMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = response;
        }

        //利用反射调用方法 如果方法返回值为null 或者 方法为void方法 则返回null
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(),paramValues);
        if (result == null || result instanceof Void) {return null;}

        //判断方法返回值是否为EModelAndView类型 如果是则返回
        boolean isEModelAndView = handlerMapping.getMethod().getReturnType() == EModelAndView.class;
        if (isEModelAndView) {
            return (EModelAndView)result;
        } else {
            return null;
        }
    }

    private Object caseStringValue(String value,Class<?> paramsType) {
        if (String.class == paramsType) {
            return value;
        } else if (Integer.class == paramsType) {
            return Integer.valueOf(value);
        } else if (Double.class == paramsType) {
            return Double.valueOf(value);
        } else {
            return value == null ? null : value;
        }
    }

}
