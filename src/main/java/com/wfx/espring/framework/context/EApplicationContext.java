package com.wfx.espring.framework.context;

import com.wfx.espring.framework.annotation.EAutowired;
import com.wfx.espring.framework.annotation.EContoller;
import com.wfx.espring.framework.annotation.EService;
import com.wfx.espring.framework.aop.EAopProxy;
import com.wfx.espring.framework.aop.ECgligAopProxy;
import com.wfx.espring.framework.aop.EJdkAopProxy;
import com.wfx.espring.framework.aop.config.EAopConfig;
import com.wfx.espring.framework.aop.support.EAdviseSupport;
import com.wfx.espring.framework.beans.EBeanWrapper;
import com.wfx.espring.framework.beans.config.EBeanDefinition;
import com.wfx.espring.framework.beans.config.EBeanPostProcessor;
import com.wfx.espring.framework.beans.support.EBeanDefinitionReader;
import com.wfx.espring.framework.beans.support.EDefaultListableBeanFactory;
import com.wfx.espring.framework.core.EBeanFactory;

import java.beans.XMLDecoder;
import java.lang.reflect.Field;
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
                //throw new Exception("The [“+ beanDefinition.getFactoryBeanName() + ”] is exists!!");
                continue;
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
            //如果不是懒加载并且bean没有创建，则直接在此创建bean
            if (!beanDefinitionEntry.getValue().isLazyIniti() && !beanDefinitionEntry.getValue().isCreated()){
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
        EBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        Object instance = null;

        //这个逻辑还不严谨，自己可以去参考Spring源码
        //工厂模式 + 策略模式
        //生成通知事件
        EBeanPostProcessor postProcessor = new EBeanPostProcessor();
        instance = instantiateBean(beanDefinition);
        if (null == instance) {return null;}

        //在实例初始化前调用通知
        postProcessor.postProcessBeforeInitialization(instance,beanName);

        //将bean用BeanWrapper封装
        EBeanWrapper beanWrapper = new EBeanWrapper(instance);
        factoryBeanInstanceCache.put(beanName,beanWrapper);

        //在实例初始化以后调用一次
        postProcessor.postProcessAfterInitialization(instance, beanName);

        //注入
        populateBean(beanWrapper);

        //设置bean信息为创建完成
        beanDefinition.setCreated(true);
        return factoryBeanInstanceCache.get(beanName).getWrapperInstance();
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }


    //DI注入
    private void populateBean(EBeanWrapper beanWrapper) {
        Object wrapperInstance = beanWrapper.getWrapperInstance();

        //利用反射进行属性注入
        Class<?> clazz = beanWrapper.getWrapperdClass();

        //判断类上是否加了注解,只有加了注解的类才能依赖注入
        if (!(clazz.isAnnotationPresent(EContoller.class) || clazz.isAnnotationPresent(EService.class))) {
            return;
        }

        //获得所有的Fields 如果field上面有EAutowired注解才进行注入
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            //设置field允许强制访问
            field.setAccessible(true);
            //如果field上面有EAutowired注解才进行注入
            if (!field.isAnnotationPresent(EAutowired.class)) {continue;}

            //获得EAutowired注解
            EAutowired autowired = field.getAnnotation(EAutowired.class);

            //获得EAutowired注解名字
            String autowiredName = autowired.value().trim();

            //如果autowiredName为“”则取类的名字
            if ("".equals(autowiredName)) {
                autowiredName = field.getType().getName();
            }

            try {
                if (this.factoryBeanInstanceCache.get(autowiredName) == null) {continue;}
                field.set(wrapperInstance,this.factoryBeanInstanceCache.get(autowiredName).getWrapperInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //将beanDefinition转换成Object方法
    private Object instantiateBean(EBeanDefinition beanDefinition) {
        //1、拿到要实例化的对象的类名
        String className = beanDefinition.getBeanClassName();

        //2、反射实例化，得到一个对象
        Object instance = null;

        try {
            //假设默认就是单例,细节暂且不考虑，先把主线拉通
            if (this.factoryBeanObjectCache.containsKey(className)) {
                //如果factoryBeanObjectCache中有直从factoryBeanObjectCache中返回instance
                instance = this.factoryBeanObjectCache.get(className);
            } else {
                //否则通过反射创建
                Class <?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                //AOP
                EAdviseSupport config = instantionAopConfig(beanDefinition);
                config.setTarget(instance);
                config.setTargetClass(clazz);

                //符合PointCut的规则的话，将代理对象
                if (config.pointCutMatch()) {
                    instance = createProxy(config).getProxy();
                }
                this.factoryBeanObjectCache.put(className,instance);
                this.factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(),instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
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

    private EAopProxy createProxy(EAdviseSupport config) {

        Class targetClass = config.getTargetClass();
        if(targetClass.getInterfaces().length > 0){
            return new EJdkAopProxy(config);
        }
        return new ECgligAopProxy(config);
    }

    private EAdviseSupport instantionAopConfig(EBeanDefinition gpBeanDefinition) {
        EAopConfig config = new EAopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new EAdviseSupport(config);
    }
}
