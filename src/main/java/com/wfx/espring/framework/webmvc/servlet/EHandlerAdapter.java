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
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.webmvc.servlet ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-18-18:18:09
 * @Description: ����
 **/
public class EHandlerAdapter {
    /**
     * �ж�EHandlerAdapter�Ƿ�֧�ִ���Object
     * @param handler
     * @return
     */
    public boolean suppports(Object handler) {return (handler instanceof EHanderMapping);}

    EModelAndView handle(HttpServletRequest request, HttpServletResponse response,Object handler) throws Exception {
        EHanderMapping handlerMapping  = (EHanderMapping) handler;

        //�ѷ������β��б��request�Ĳ���һһ��Ӧ
        Map<String,Integer> paramIndexingMapping = new HashMap<>();

        /**
         * ��ȡ�����м���ע��Ĳ���
         * �ѷ����ϵ�ע���õ����õ�����һ����ά����
         * ��Ϊһ�������ж��������һ�������ֿ����ж��ע��
         */
        Annotation[][] params = handlerMapping.getMethod().getParameterAnnotations();
        //����������ÿ������
        for (int i = 0; i < params.length; i++) {
            //����������ע�⣬Ȼ���жϸ�ע���Ƿ���ERequestMapping
            for (Annotation annotation : params[i]) {
                //�жϸ�ע���Ƿ���ERequestMapping
                if (annotation.annotationType().equals(ERequestParam.class)) {
                    //���˵��ERequestMapping �򽫸ò��������ƺ���request�����б��λ��i
                    // ��ӽ�paramIndexingMapping��ȥ
                    String paramName = ((ERequestParam)annotation).value();
                    if (!"".equals(paramName.trim())) {
                        paramIndexingMapping.put(paramName,i);
                    }
                }
            }
        }

        //��ȡ�����е�request��response����
        Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();
        //����parameterTypes
        for (int i = 0; i < paramTypes.length; i++) {
            //���������HttpServletRequest���ͻ���HttpServletResponse����
            //�����ǵ����ƺͶ�Ӧ��λ��i����paramIndexingMapping��
            Class<?> type = paramTypes[i];
            if (type ==  HttpServletRequest.class || type == HttpServletResponse.class) {
                paramIndexingMapping.put(type.getName(), i);
            }
        }

        //��÷������β��б�
        Map<String,String[]> paramMap = request.getParameterMap();

        //ʵ���б�
        Object[] paramValues = new Object[paramTypes.length];

        //��paramIndexingMapping�е��βεĲ���ֵ����ʵ���б���
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            String value = Arrays.toString(entry.getValue()).replaceAll("\\[","")
                    .replaceAll("\\]","")
                    .replaceAll("\\s",",");

            //�����������paramIndexingMapping������
            if (!paramIndexingMapping.containsKey(entry.getKey())) {continue;}

            //���򽫲�����ӽ�ʵ���б���
            int index = paramIndexingMapping.get(entry.getKey());
            paramValues[index] = caseStringValue(value,paramTypes[index]);
        }

        //�������е�request������ӽ�ʵ���б���
        if (paramIndexingMapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = paramIndexingMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = request;
        }

        //�������е�response�������ʵ���б���
        if (paramIndexingMapping.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = paramIndexingMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = response;
        }

        //���÷�����÷��� �����������ֵΪnull ���� ����Ϊvoid���� �򷵻�null
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(),paramValues);
        if (result == null || result instanceof Void) {return null;}

        //�жϷ�������ֵ�Ƿ�ΪEModelAndView���� ������򷵻�
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
