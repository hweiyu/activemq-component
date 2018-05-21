package com.hwy.mqcomponent.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;

/**
 * @author
 */
@Component
public class BeanFactoryHolder implements BeanFactoryAware {

    private static BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        BeanFactoryHolder.beanFactory = beanFactory;
    }

    public static BeanFactory get() {
        return BeanFactoryHolder.beanFactory;
    }
}
