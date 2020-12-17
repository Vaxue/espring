package com.wfx.espring.framework.context;

import com.wfx.espring.framework.beans.EBeanWrapper;
import com.wfx.espring.framework.beans.config.EBeanDefinition;
import com.wfx.espring.framework.beans.support.EBeanDefinitionReader;
import com.wfx.espring.framework.beans.support.EDefaultListableBeanFactory;
import com.wfx.espring.framework.core.EBeanFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @BelongsProject:espring 所属项目
 * @BelongsPackage:com.wfx.espring.framework.context.support 所属包
 * @Author:28050 作者
 * @CreateTime: 2020-12-15-18:43:25
 * @Description: 描述
 **/
public class EApplicationContext extends EDefaultListableBeanFactory implements EBeanFactory {
    private String[] configLoactions; //Properties 配置文件的位置
    private EBeanDefinitionReader reader; //读取配置文件的实现类

    //单例的IOC容器缓存
    private Map<String,Object> factoryBeanObjectCache = new ConcurrentHashMap<>();
    //通用的IOC容器
    private Map<String, EBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();


    //通过构造函数传入Properties配置文件的位置
    public EApplicationContext(String... configLoactions) {
        this.configLoactions = configLoactions;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载容器
     * @throws Exception
     */
    @Override
    public void refresh() throws Exception {
        //1、定位，定位配置文件
        reader = new EBeanDefinitionReader(this.configLoactions);
        //2、加载配置文件，扫描相关的类，把它们封装成BeanDefinition
        List<EBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        //3、注册，把配置信息放到容器里面(伪IOC容器)
        doRegisterBeanDefinition(beanDefinitions);
        //4、把不是延时加载的类，有提前初始化
        doAutowrited();
    }

    /**
     * 注册，把配置信息放到容器里面(伪IOC容器)
     * @param beanDefinitions
     */
    private void doRegisterBeanDefinition(List<EBeanDefinition> beanDefinitions) throws Exception {
        for (EBeanDefinition beanDefinition : beanDefinitions) {
            //如果伪IOC容器中已经存在，提示信息
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The [“+ beanDefinition.getFactoryBeanName() + ”] is exists!!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }
        //容器初始化结束
    }

    /**
     * 把不是延时加载的类，有提前初始化
     */
    private void doAutowrited() {
        for (Map.Entry<String, EBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            //如果不是懒加载，则直接在此创建bean
            if (!beanDefinitionEntry.getValue().isLazyIniti()){
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 依赖注入，从这里开始，通过读取BeanDefinition中的信息
     * 然后，通过反射机制创建一个实例并返回
     * Spring做法是，不会把最原始的对象放出去，会用一个BeanWrapper来进行一次包装
     * 装饰器模式：
     * 1、保留原来的OOP关系
     * 2、我需要对它进行扩展，增强（为了以后AOP打基础）
     * @param beanName
     * @return
     * @throws Exception
     */
    @Override
    public Object getBean(String beanName) throws Exception {
        return null;
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    /**
     *获取beanDefinitionMap中的key数组
     * @return
     */
    public String[] getBeanDefitioNames() {
        return super.beanDefinitionMap.keySet().toArray(new String[super.beanDefinitionMap.size()]);
    }

    /**
     * 得到beanDefinitionMap的容量
     * @return
     */
    public int getBeanDefinitionCount() {
        return super.beanDefinitionMap.size();
    }

    /**
     * 得到配置信息
     * @return
     */
    public Properties getConfig() {
        return this.reader.getConfig();
    }
}
