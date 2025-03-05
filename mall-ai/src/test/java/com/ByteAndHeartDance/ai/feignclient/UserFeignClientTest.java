package com.ByteAndHeartDance.ai.feignclient;

import feign.RequestInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
public class UserFeignClientTest {
  @Autowired
  private UserFeignClient userFeignClient;

  @Test
  void getUserDetail() {
    //userFeignClient.getUserDetail();
  }
}
