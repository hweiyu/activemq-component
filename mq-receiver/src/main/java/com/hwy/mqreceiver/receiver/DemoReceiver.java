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
    public void message(@ParamMapping DemoBean bean) {
        System.out.println("=========={demo.message} bean <" + bean + ">");
    }

    @JmsListener(destination = "demo.message2")
    public void message2(@ParamMapping DemoBean bean) {
        System.out.println("=========={demo.message2} bean <" + bean + ">");
    }

    @JmsListener(destination = "demo.message3")
    public void message3(@ParamMapping DemoBean bean) {
        System.out.println("=========={demo.message3} bean <" + bean + ">");
    }

    @JmsListener(destination = "demo.message4")
    public void message4(@ParamMapping DemoBean bean) {
        System.out.println("=========={demo.message4} bean <" + bean + ">");
    }

    @JmsListener(destination = "demo.message5")
    public void message5(@ParamMapping DemoBean bean) {
        System.out.println("=========={demo.message5} bean <" + bean + ">");
    }

    @JmsListener(destination = "demo.message6")
    public void message6(@ParamMapping DemoBean bean) {
        System.out.println("=========={demo.message6} bean <" + bean + ">");
    }
}
