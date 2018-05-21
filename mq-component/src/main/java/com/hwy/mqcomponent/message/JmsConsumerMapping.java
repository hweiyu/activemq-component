package com.hwy.mqcomponent.message;

import java.util.Map;

/**
 * 消息转换映射对象
 *
 */
public interface JmsConsumerMapping {
    /**
     * 获取自定义的消息转换实现类
     * @param key 消息目的地名称
     * @return 如果存在则返回class，否则返回null
     */
    Class<?> getMappingClass(String key);

    /**
     * 集合中，是否存在list参数化类型
     * @param key 消息目的地名称
     * @return 如果存在则返回：true，否则返回：false
     */
    boolean isListParameterType(String key);

    /**
     * 集合中，返回list的参数化类型
     * @param key 消息目的地名称
     * @return 如果存在则返回class，否则返回null
     */
    Class<?> getListParameterType(String key);

    /**
     * map中，是否存在map的参数化类型
     * @param key 消息目的地名称
     * @return 如果存在则返回：true，否则返回：false
     */
    boolean isMapParameterType(String key);

    /**
     * map中，返回map的参数化类型
     * @param key 消息目的地地址
     * @return 如果存在则返回Map.Entry<Class<?>, Class<?>> 数据，否则返回：null
     */
    Map.Entry<Class<?>, Class<?>> getMapParameterType(String key);
}
