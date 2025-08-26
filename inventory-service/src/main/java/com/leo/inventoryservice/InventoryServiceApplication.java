package com.leo.inventoryservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableTransactionManagement
@MapperScan("com.leo.inventoryservice.mapper")
@ComponentScan(basePackages = {"com.leo"})
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
        System.out.println("""
            
            ╦╔╗╔╦  ╦╔═╗╔╗╔╔╦╗╔═╗╦═╗╦ ╦
            ║║║║╚╗╔╝║╣ ║║║ ║ ║ ║╠╦╝╚╦╝
            ╩╝╚╝ ╚╝ ╚═╝╝╚╝ ╩ ╚═╝╩╚═ ╩ 
            
            :: Leo Mall Inventory Service Started Successfully ::
            """);
    }

}
