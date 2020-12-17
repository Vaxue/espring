package com.wfx.espring.framework.beans.support;

import com.wfx.espring.framework.beans.config.EBeanDefinition;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.beans.support ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-15-18:48:42
 * @Description: ����
 **/
@Data
public class EBeanDefinitionReader {

    private List<String> registryBeanClasses = new ArrayList<>();

    private Properties config = new Properties();

    /**�������ļ��е�key,�����xml�Ĺ淶*/
    private final String SCAN_PACKAGE = "scanPackage";

   public EBeanDefinitionReader(String... locations) throws FileNotFoundException {
       //ͨ��URL��λ�ҵ�������Ӧ���ļ�,Ȼ��ת��Ϊ�ļ���
       InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:",""));
       try {
           config.load(is);
       } catch (IOException e) {
           e.printStackTrace();
       } finally {
           if (null != is) {
               try {
                   is.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
       doScanner(config.getProperty(SCAN_PACKAGE));
   }

   private void doScanner(String scanPackage) {
       //ת��Ϊ�ļ�·��,ʵ���Ͼ��ǰ�.�滻Ϊ/��OK��
       URL url = this.getClass().getClassLoader().getResource(scanPackage.replaceAll("\\.","/"));
       File classPath = new File(url.getFile());
       for (File file : classPath.listFiles()) {
           if (file.isDirectory()) {
               doScanner(scanPackage + "." + file.getName());
           } else {
               if (!file.getName().endsWith(".class")) {continue;}
               String className = (scanPackage + "." + file.getName().replace(".class",""));
               registryBeanClasses.add(className);
           }
       }
   }

    /**�������ļ���ɨ�赽�����е�������Ϣת��ΪEBeanDefinition�����Ա���֮��IOC��������*/
    public List<EBeanDefinition> loadBeanDefinitions(){
        List<EBeanDefinition> result = new ArrayList<>();

        try {
            for (String className : registryBeanClasses) {
                //1.�������������ȡClass����
                Class<?> beanClass = Class.forName(className);
                //2.����ǽӿ��������ʵ�������ʵ����
                if (beanClass.isInterface()) {
                    continue;
                }
                //3.benaNameĬ��Ϊ��������ĸСд
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));
                //4.����ʵ�ֵĽӿ�Ҳ�ŵ�������
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces) {
                    //����Ƕ��ʵ���ֻ࣬�ܸ���
                    result.add(doCreateBeanDefinition(i.getName(),beanClass.getName()));
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    private EBeanDefinition doCreateBeanDefinition(String beanClassName, String factoryBeanName) {
        EBeanDefinition beanDefinition = new EBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    private String toLowerFirstCase(String s) {
      return s.substring(0,1).toLowerCase() + s.substring(1);
    }

}

