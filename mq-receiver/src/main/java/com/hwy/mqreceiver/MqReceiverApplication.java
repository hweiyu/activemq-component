package com.hwy.mqreceiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.hwy")
@SpringBootApplication
public class MqReceiverApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqReceiverApplication.class, args);
    }
}
