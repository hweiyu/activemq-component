package com.hwy.mqcomponent.message;

import javax.jms.Destination;

/**
 * 消息目的地解析器
 *
 * @author
 */
public interface DestinationParse {

    String parse(Destination destination);
}
