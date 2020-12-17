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
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.beans.support 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-15-18:48:42
 * @Description: 描述
 **/
@Data
public class EBeanDefinitionReader {

    private List<String> registryBeanClasses = new ArrayList<>();

    private Properties config = new Properties();

    /**定配置文件中的key,相对于xml的规范*/
    private final String SCAN_PACKAGE = "scanPackage";

   public EBeanDefinitionReader(String... locations) throws FileNotFoundException {
       //通过URL定位找到其所对应的文件,然后转换为文件流
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
       //转换为文件路径,实际上就是把.替换为/就OK了
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

    /**把配置文件中扫描到的所有的配置信息转换为EBeanDefinition对象，以便于之后IOC操作方便*/
    public List<EBeanDefinition> loadBeanDefinitions(){
        List<EBeanDefinition> result = new ArrayList<>();

        try {
            for (String className : registryBeanClasses) {
                //1.根据类名反射获取Class对象
                Class<?> beanClass = Class.forName(className);
                //2.如果是接口则对它的实现类进行实例化
                if (beanClass.isInterface()) {
                    continue;
                }
                //3.benaName默认为类名首字母小写
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));
                //4.该类实现的接口也放到容器中
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces) {
                    //如果是多个实现类，只能覆盖
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

