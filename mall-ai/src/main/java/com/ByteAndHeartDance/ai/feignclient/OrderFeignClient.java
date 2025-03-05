package com.ByteAndHeartDance.ai.feignclient;

import com.ByteAndHeartDance.ai.configuration.FeignClientConfig;
import com.ByteAndHeartDance.ai.dto.CreateTradeDirectRequest;
import com.ByteAndHeartDance.ai.dto.CreateTradeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "mall-order-api",
    path = "/v1/trade",
    configuration = FeignClientConfig.class,
    url = "http://localhost:8023")
public interface OrderFeignClient {
  @PostMapping("/createByDirect")
  CreateTradeResponse createByDirect(
      @RequestHeader("Authorization") String token,
      @RequestBody CreateTradeDirectRequest createTradeDirectDTO);
}
