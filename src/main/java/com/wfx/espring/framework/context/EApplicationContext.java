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
                throw new Exception("The [��+ beanDefinition.getFactoryBeanName() + ��] is exists!!");
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
            //������������أ���ֱ���ڴ˴���bean
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
        return null;
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
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
}
