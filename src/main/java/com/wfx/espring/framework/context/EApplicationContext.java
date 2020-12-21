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
 * @BelongsProject:espring ������Ŀ
 * @BelongsPackage:com.wfx.espring.framework.context.support ������
 * @Author:28050 ����
 * @CreateTime: 2020-12-15-18:43:25
 * @Description: ����
 **/
public class EApplicationContext extends EDefaultListableBeanFactory implements EBeanFactory {
    private String[] configLoactions; //Properties �����ļ���λ��
    private EBeanDefinitionReader reader; //��ȡ�����ļ���ʵ����

    //������IOC��������
    private Map<String,Object> factoryBeanObjectCache = new ConcurrentHashMap<>();
    //ͨ�õ�IOC����
    private Map<String, EBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();


    //ͨ�����캯������Properties�����ļ���λ��
    public EApplicationContext(String... configLoactions) {
        this.configLoactions = configLoactions;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ��������
     * @throws Exception
     */
    @Override
    public void refresh() throws Exception {
        //1����λ����λ�����ļ�
        reader = new EBeanDefinitionReader(this.configLoactions);
        //2�����������ļ���ɨ����ص��࣬�����Ƿ�װ��BeanDefinition
        List<EBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        //3��ע�ᣬ��������Ϣ�ŵ���������(αIOC����)
        doRegisterBeanDefinition(beanDefinitions);
        //4���Ѳ�����ʱ���ص��࣬����ǰ��ʼ��
        doAutowrited();
    }

    /**
     * ע�ᣬ��������Ϣ�ŵ���������(αIOC����)
     * @param beanDefinitions
     */
    private void doRegisterBeanDefinition(List<EBeanDefinition> beanDefinitions) throws Exception {
        for (EBeanDefinition beanDefinition : beanDefinitions) {
            //���αIOC�������Ѿ����ڣ���ʾ��Ϣ
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                //throw new Exception("The [��+ beanDefinition.getFactoryBeanName() + ��] is exists!!");
                continue;
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }
        //������ʼ������
    }

    /**
     * �Ѳ�����ʱ���ص��࣬����ǰ��ʼ��
     */
    private void doAutowrited() {
        for (Map.Entry<String, EBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            //������������ز���beanû�д�������ֱ���ڴ˴���bean
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
     * ����ע�룬�����￪ʼ��ͨ����ȡBeanDefinition�е���Ϣ
     * Ȼ��ͨ��������ƴ���һ��ʵ��������
     * Spring�����ǣ��������ԭʼ�Ķ���ų�ȥ������һ��BeanWrapper������һ�ΰ�װ
     * װ����ģʽ��
     * 1������ԭ����OOP��ϵ
     * 2������Ҫ����������չ����ǿ��Ϊ���Ժ�AOP�������
     * @param beanName
     * @return
     * @throws Exception
     */
    @Override
    public Object getBean(String beanName) throws Exception {
        EBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        Object instance = null;

        //����߼������Ͻ����Լ�����ȥ�ο�SpringԴ��
        //����ģʽ + ����ģʽ
        //����֪ͨ�¼�
        EBeanPostProcessor postProcessor = new EBeanPostProcessor();
        instance = instantiateBean(beanDefinition);
        if (null == instance) {return null;}

        //��ʵ����ʼ��ǰ����֪ͨ
        postProcessor.postProcessBeforeInitialization(instance,beanName);

        //��bean��BeanWrapper��װ
        EBeanWrapper beanWrapper = new EBeanWrapper(instance);
        factoryBeanInstanceCache.put(beanName,beanWrapper);

        //��ʵ����ʼ���Ժ����һ��
        postProcessor.postProcessAfterInitialization(instance, beanName);

        //ע��
        populateBean(beanWrapper);

        //����bean��ϢΪ�������
        beanDefinition.setCreated(true);
        return factoryBeanInstanceCache.get(beanName).getWrapperInstance();
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }


    //DIע��
    private void populateBean(EBeanWrapper beanWrapper) {
        Object wrapperInstance = beanWrapper.getWrapperInstance();

        //���÷����������ע��
        Class<?> clazz = beanWrapper.getWrapperdClass();

        //�ж������Ƿ����ע��,ֻ�м���ע������������ע��
        if (!(clazz.isAnnotationPresent(EContoller.class) || clazz.isAnnotationPresent(EService.class))) {
            return;
        }

        //������е�Fields ���field������EAutowiredע��Ž���ע��
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            //����field����ǿ�Ʒ���
            field.setAccessible(true);
            //���field������EAutowiredע��Ž���ע��
            if (!field.isAnnotationPresent(EAutowired.class)) {continue;}

            //���EAutowiredע��
            EAutowired autowired = field.getAnnotation(EAutowired.class);

            //���EAutowiredע������
            String autowiredName = autowired.value().trim();

            //���autowiredNameΪ������ȡ�������
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

    //��beanDefinitionת����Object����
    private Object instantiateBean(EBeanDefinition beanDefinition) {
        //1���õ�Ҫʵ�����Ķ��������
        String className = beanDefinition.getBeanClassName();

        //2������ʵ�������õ�һ������
        Object instance = null;

        try {
            //����Ĭ�Ͼ��ǵ���,ϸ�����Ҳ����ǣ��Ȱ�������ͨ
            if (this.factoryBeanObjectCache.containsKey(className)) {
                //���factoryBeanObjectCache����ֱ��factoryBeanObjectCache�з���instance
                instance = this.factoryBeanObjectCache.get(className);
            } else {
                //����ͨ�����䴴��
                Class <?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                //AOP
                EAdviseSupport config = instantionAopConfig(beanDefinition);
                config.setTarget(instance);
                config.setTargetClass(clazz);

                //����PointCut�Ĺ���Ļ������������
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
     *��ȡbeanDefinitionMap�е�key����
     * @return
     */
    public String[] getBeanDefitioNames() {
        return super.beanDefinitionMap.keySet().toArray(new String[super.beanDefinitionMap.size()]);
    }

    /**
     * �õ�beanDefinitionMap������
     * @return
     */
    public int getBeanDefinitionCount() {
        return super.beanDefinitionMap.size();
    }

    /**
     * �õ�������Ϣ
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
