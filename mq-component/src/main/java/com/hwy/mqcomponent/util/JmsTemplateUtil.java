package com.hwy.mqcomponent.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwy.mqcomponent.configuration.ApplicationHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * 发送消息工具类
 */
public class JmsTemplateUtil {

    private final static Logger LOG = LoggerFactory.getLogger(JmsTemplateUtil.class);

    private static volatile JmsTemplate jmsTemplate;

    private static JmsTemplate getJmsTemplate() {
        if (jmsTemplate == null) {
            synchronized (JmsTemplateUtil.class) {
                if (jmsTemplate == null) {
                    jmsTemplate = ApplicationHolder.getBean(JmsTemplate.class);
                }
            }
        }
        return jmsTemplate;
    }

    /**
     * 发送消息
     * @param destination 队列
     * @param message     message
     */
    public static void send(String destination, Object message) {
        try {
            getJmsTemplate().convertAndSend(destination, message);
            LOG.info("消息发送成功，dest：{}，message：{}", destination, new ObjectMapper().writeValueAsString(message));
        } catch (Throwable t) {
            LOG.error("消息发送失败，dest:{}，错误信息为：", destination, t);
        }
    }
}
