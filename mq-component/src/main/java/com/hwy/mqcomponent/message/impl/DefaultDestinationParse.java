package com.hwy.mqcomponent.message.impl;

import com.hwy.mqcomponent.message.DestinationParse;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTempQueue;
import org.apache.activemq.command.ActiveMQTempTopic;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.Destination;
import javax.jms.JMSException;

/**
 * @author
 */
public class DefaultDestinationParse implements DestinationParse {
    @Override
    public String parse(Destination destination) {
        try {
            if (destination instanceof ActiveMQQueue) {
                return ((ActiveMQQueue) destination).getQueueName();
            } else if (destination instanceof ActiveMQTopic) {
                return ((ActiveMQTopic)destination).getTopicName();
            } else if (destination instanceof ActiveMQTempQueue) {
                return ((ActiveMQTempQueue) destination).getQueueName();
            } else if (destination instanceof ActiveMQTempTopic) {
                return ((ActiveMQTempTopic) destination).getTopicName();
            }
        } catch (JMSException e) {
            throw new IllegalArgumentException("目的地解析失败，获取消息目的地名称失败，当前堆栈信息为：" + e);
        }
        throw new IllegalArgumentException("目的地解析失败，仅仅支持[ActiveMQQueue]或者[ActiveMQTopic]消息目的地, 而当前的目的地址为" + destination);
    }
}
