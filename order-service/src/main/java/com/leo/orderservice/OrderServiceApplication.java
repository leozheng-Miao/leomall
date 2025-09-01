package com.leo.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.leo.orderservice", "com.leo"})
@EnableDiscoveryClient
@EnableFeignClients
@EnableTransactionManagement
@EnableScheduling
@EnableAsync
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
        System.out.println("\n" +
                "╔═╗╦═╗╔╦╗╔═╗╦═╗  ╔═╗╔═╗╦═╗╦  ╦╦╔═╗╔═╗\n" +
                "║ ║╠╦╝ ║║║╣ ╠╦╝  ╚═╗║╣ ╠╦╝╚╗╔╝║║  ║╣ \n" +
                "╚═╝╩╚══╩╝╚═╝╩╚═  ╚═╝╚═╝╩╚═ ╚╝ ╩╚═╝╚═╝\n" +
                "订单服务启动成功！端口：8006\n");
    }
}
