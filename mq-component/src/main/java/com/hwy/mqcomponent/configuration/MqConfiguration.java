package com.hwy.mqcomponent.configuration;

import com.hwy.mqcomponent.exception.ExceptionHandler;
import com.hwy.mqcomponent.message.CustomDefinitionMappingJackson;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;

/**
 *
 *  @author
 */

@EnableJms
@Configuration
@AutoConfigureAfter(value = {ApplicationHolder.class, BeanFactoryHolder.class})
public class MqConfiguration {

    @Value("${spring.mq.url}")
    private String url;


    @Bean
    public ActiveMQConnectionFactory amqConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(url);
        return factory;
    }

    @Bean
    public MappingJackson2MessageConverter messageConverter() {
        return new CustomDefinitionMappingJackson();
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(amqConnectionFactory());
        factory.setSessionTransacted(true);
        factory.setSessionAcknowledgeMode(1);
        factory.setMessageConverter(messageConverter());
        factory.setErrorHandler(new ExceptionHandler());
        factory.setConcurrency("1-5");
        factory.setPubSubDomain(true);
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setSessionTransacted(true);
        jmsTemplate.setSessionAcknowledgeMode(1);
        jmsTemplate.setConnectionFactory(amqConnectionFactory());
        jmsTemplate.setMessageConverter(messageConverter());
        jmsTemplate.setPubSubDomain(true);
        return jmsTemplate;
    }

}
