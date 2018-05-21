package com.hwy.mqcomponent.message.impl;

import com.hwy.mqcomponent.annotation.CustomMqListener;
import com.hwy.mqcomponent.annotation.ParamMapping;
import com.hwy.mqcomponent.configuration.ApplicationHolder;
import com.hwy.mqcomponent.configuration.BeanFactoryHolder;
import com.hwy.mqcomponent.message.JmsConsumerMapping;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.jms.annotation.JmsListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 */
public class DefaultJmsConsumer implements JmsConsumerMapping {

    /**
     * 自定义类型映射
     */
    private Map<String, Class<?>> classMapping = new HashMap<>();

    /**
     * 集合参数化类型映射
     */
    private Map<String, Class<?>> listParameterType = new HashMap<>();

    /**
     * map参数化类型映射
     */
    private Map<String, Map.Entry<Class<?>, Class<?>>> mapParameterType = new HashMap<>();

    public DefaultJmsConsumer() {
        init();
    }

    @Override
    public Class<?> getMappingClass(String key) {
        return classMapping.get(key);
    }

    @Override
    public boolean isListParameterType(String key) {
        return listParameterType.containsKey(key);
    }

    @Override
    public Class<?> getListParameterType(String key) {
        return listParameterType.get(key);
    }

    @Override
    public boolean isMapParameterType(String key) {
        return mapParameterType.containsKey(key);
    }

    @Override
    public Map.Entry<Class<?>, Class<?>> getMapParameterType(String key) {
        return mapParameterType.get(key);
    }

    private List<String> getCustomAnnotation() {
        return getBeansWithAnnotation();
    }

    private List<String> getBeansWithAnnotation() {
        List<String> beans = new ArrayList<>();
        ConfigurableListableBeanFactory factory = (ConfigurableListableBeanFactory) BeanFactoryHolder.get();
        for(String name : factory.getBeanDefinitionNames()) {
            BeanDefinition bd = factory.getBeanDefinition(name);
            if( bd instanceof ScannedGenericBeanDefinition) {
                boolean has = ((ScannedGenericBeanDefinition)bd).getMetadata().hasAnnotation(CustomMqListener.class.getName());
                if (has) {
                    beans.add(name);
                }
            }
        }
        return beans;
    }

    private void init() {
        List<String> beans = getCustomAnnotation();
        for (String name : beans) {
            Object bean = ApplicationHolder.getBean(name);
            Method[] methods = bean.getClass().getDeclaredMethods();
            for (Method method : methods) {
                JmsListener jmsListener = method.getAnnotation(JmsListener.class);
                if (jmsListener == null) {
                    continue;
                }
                // 检查是否存在自定义的消息解析器注解
                final String destination = jmsListener.destination();
                int annotationIndex = -1;
                Annotation[][] annotations = method.getParameterAnnotations();
                for (int i = 0; i <annotations.length; i++) {
                    for (Annotation a : annotations[i]) {
                        if (a.annotationType().isAssignableFrom(ParamMapping.class)) {
                            annotationIndex = i;
                        }
                    }
                }
                // 如果不存在自定义注解则返回
                if (annotationIndex < 0) {
                    continue;
                }
                Class<?> cls = method.getParameterTypes()[annotationIndex];
                classMapping.put(destination, cls);
                // 检查是否list集合的参数化类型
                if (cls.isAssignableFrom(List.class)) {
                    Type type = method.getGenericParameterTypes()[annotationIndex];
                    Class<?> typeArgClass = getListParameterClassFromType(type);
                    if (typeArgClass != null) {
                        listParameterType.put(destination, typeArgClass);
                    }
                } else // 检查是否map集合的参数化类型
                    if (cls.isAssignableFrom(Map.class)) {
                        Type type = method.getGenericParameterTypes()[annotationIndex];
                        Map.Entry<Class<?>, Class<?>> typeArgClass =  getMapParameterClassFromType(type);
                        if (typeArgClass != null) {
                            mapParameterType.put(destination, typeArgClass);
                        }
                    }
            }
        }

    }

    /**
     * 获取list参数化类型的实际类型
     * 仅仅获取一个层级的，嵌套的不支持
     * @param type
     * @return
     */
    private Class<?> getListParameterClassFromType(Type type) {
        if(type instanceof ParameterizedType){
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            return (Class<?>)typeArguments[0];
        }
        return null;
    }

    /**
     * 获取map参数化类型
     * 仅仅获取第一个层级的，不支持嵌套
     * @param type
     * @return
     */
    private Map.Entry<Class<?>, Class<?>> getMapParameterClassFromType(Type type) {
        if(type instanceof ParameterizedType){
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            return new MapClassEntry<Class<?>, Class<?>>((Class<?>)typeArguments[0], (Class<?>)typeArguments[1]);
        }
        return null;
    }


    class MapClassEntry<K, V> implements Map.Entry<K, V> {
        K key;
        V value;
        public MapClassEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public V setValue(Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }
    }
}
