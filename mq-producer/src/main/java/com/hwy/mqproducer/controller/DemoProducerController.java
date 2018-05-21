package com.hwy.mqproducer.controller;

import com.hwy.mqcomponent.util.JmsTemplateUtil;
import com.hwy.mqproducer.bean.DemoBean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangweiyu
 * @version V1.0
 * @Title: 描述
 * @Description: 描述
 * @date 2018/5/21 11:12
 **/
@RestController
public class DemoProducerController {

    @RequestMapping(value = "producer/{message}")
    public String producer(@PathVariable("message") String message) {
        JmsTemplateUtil.send("demo.message", DemoBean.builder().message(message).build());
        return "message : " + message + " send success";
    }
}
