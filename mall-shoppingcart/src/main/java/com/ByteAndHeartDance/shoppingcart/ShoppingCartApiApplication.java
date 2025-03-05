package com.ByteAndHeartDance.shoppingcart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
// 允许服务注册到nacos与发现其他服务
@EnableDiscoveryClient
@MapperScan(basePackages = "com.ByteAndHeartDance.shoppingcart.mapper")
@EnableCaching
@EnableFeignClients(basePackages = {"com.ByteAndHeartDance.shoppingcart.feign"})
@SpringBootApplication(scanBasePackages = {"com.ByteAndHeartDance"})
public class ShoppingCartApiApplication {
  public static void main(String[] args) {
    SpringApplication.run(ShoppingCartApiApplication.class, args);
  }
}
