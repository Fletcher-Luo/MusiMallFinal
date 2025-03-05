package com.ByteAndHeartDance.ai.feignclient;

import com.ByteAndHeartDance.ai.configuration.FeignClientConfig;
import com.ByteAndHeartDance.ai.entity.UserWebEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "mall-auth-api",
    path = "v1/web/user",
    configuration = FeignClientConfig.class,
    url = "http://localhost:8021")
public interface UserFeignClient {
  @GetMapping(value = "/getUserDetail")
  UserWebEntity getUserDetail(@RequestHeader("Authorization") String token);
}
