package com.hwy.mqcomponent.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

/**
 * @author
 */
public class ExceptionHandler implements ErrorHandler {

     private static Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public void handleError(Throwable t) {
        logger.error("消息消费失败，当前错误堆栈信息为：", t);
    }
}
