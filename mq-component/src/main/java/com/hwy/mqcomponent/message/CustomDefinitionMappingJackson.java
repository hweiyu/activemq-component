package com.hwy.mqcomponent.message;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwy.mqcomponent.message.impl.DefaultDestinationParse;
import com.hwy.mqcomponent.message.impl.DefaultJmsConsumer;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConversionException;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扩展消息转换器
 *
 * @author
 */
public class CustomDefinitionMappingJackson extends MappingJackson2MessageConverter {
    /**
     * 消息映射关系
     */
    private final JmsConsumerMapping jmsConsumerMapping = new DefaultJmsConsumer();

    /**
     * 消息目的地解析器
     */
    private final DestinationParse destinationParse = new DefaultDestinationParse();

    /**
     * 默认的消息头属性值，在发送消息时，会将消息体的class属性设置到message.setStringProperty内容中
     * 然后在解析时重新渲染对应的属性。而有些场景是仅仅发送空消息，没有消息数据，仅仅做一个提示作用而已
     */
    private final String DEFAULT_MESSAGE_PROPERTY = "defaultMessageProperty";

    /**
     *  spring 规定的用于系统与系统之间的交互标识
     */
    private final String DEFAULT_TYPE_PROPERTY_NAME = DEFAULT_MESSAGE_PROPERTY;

    /**
     * 缓存javaType对象，减少系统消耗
     */
    private Map<String, JavaType> javaTypeCache = new ConcurrentHashMap<>();

    private ObjectMapper objectMapper;

    public CustomDefinitionMappingJackson() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        super.setObjectMapper(objectMapper);
        super.setTypeIdPropertyName(DEFAULT_TYPE_PROPERTY_NAME);
    }

    /**
     * 重写jackson解析消息工厂
     * @param message the JMS Message to set the type id on
     * @return  JavaType
     */
    @Override
    public JavaType getJavaTypeForMessage(Message message) throws JMSException {
        return getFromCache(message);
    }

    private JavaType getFromCache(Message message)  throws JMSException {
        final String destination = destinationParse.parse(message.getJMSDestination());
        JavaType javaType = javaTypeCache.get(destination);
        if (javaType != null) {
            return javaType;
        }
        if (jmsConsumerMapping.isListParameterType(destination)) {
            JavaType parameterType = this.objectMapper.getTypeFactory().constructType(jmsConsumerMapping.getListParameterType(destination));
            javaType = this.objectMapper.getTypeFactory().constructParametricType(jmsConsumerMapping.getMappingClass(destination), parameterType);

        } else if (jmsConsumerMapping.isMapParameterType(destination)) {
            Map.Entry<Class<?>, Class<?>> mapClass = jmsConsumerMapping.getMapParameterType(destination);
            javaType = this.objectMapper.getTypeFactory().constructMapType(Map.class, mapClass.getKey(), mapClass.getValue());

        } else {
            Class<?> cls = jmsConsumerMapping.getMappingClass(destination);
            javaType = cls == null ? super.getJavaTypeForMessage(message): this.objectMapper.getTypeFactory().constructType(cls);
        }
        javaTypeCache.put(destination, javaType);
        return javaType;
    }

    /**
     * 如果发现消息内容为空，则改为一个默认的消息内容
     * @param message the JMS Message to set the type id on
     * @return Object
     */
    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        return isNullMessage(message) ? DEFAULT_MESSAGE_PROPERTY : super.fromMessage(message);
    }

    /**
     * 如果发现消息内容为空，则设置一个默认的消息头，防止spring解析式，需要头消息
     * @param object the payload object to set a type id for
     * @param message the JMS Message to set the type id on
     */
    @Override
    protected void setTypeIdOnMessage(Object object, Message message) throws JMSException {
        if (object != null) {
            super.setTypeIdOnMessage(object, message);
        } else {
            message.setStringProperty(DEFAULT_TYPE_PROPERTY_NAME, DEFAULT_MESSAGE_PROPERTY);
        }
    }

    /**
     * 检查是否为空的消息体
     * 1，发送方默认发送了null的消息体
     * 2，定时任务发送的空消息体
     * @param message the JMS Message to set the type id on
     * @return boolean
     */
    private boolean isNullMessage(Message message) {
        try {
            String prop = message.getStringProperty(DEFAULT_TYPE_PROPERTY_NAME);
            return prop == null || prop.isEmpty() || prop.equals(DEFAULT_MESSAGE_PROPERTY);
        } catch (JMSException e) {
            throw new MessageConversionException("Failed to get JSON head property", e);
        }
    }
}
