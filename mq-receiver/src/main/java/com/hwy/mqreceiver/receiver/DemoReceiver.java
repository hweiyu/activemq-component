package com.hwy.mqreceiver.receiver;

import com.hwy.mqcomponent.annotation.CustomMqListener;
import com.hwy.mqcomponent.annotation.ParamMapping;
import com.hwy.mqreceiver.bean.DemoBean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * @author huangweiyu
 * @version V1.0
 * @Title: 描述
 * @Description: 描述
 * @date 2018/5/21 11:25
 **/
@CustomMqListener
@Component
public class DemoReceiver {

    @JmsListener(destination = "demo.message")
    public void bean(@ParamMapping DemoBean bean) {
        System.out.println("==========receiver bean <" + bean + ">");
    }
}
