package com.ByteAndHeartDance.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@MapperScan("com.ByteAndHeartDance.order.mapper")
@EnableCaching
@EnableFeignClients
@EnableHystrix
@SpringBootApplication(scanBasePackages = {"com.ByteAndHeartDance"})
public class OrderApiApplication {
  public static void main(String[] args) {
    SpringApplication.run(OrderApiApplication.class, args);
  }
}
