package com.ByteAndHeartDance.ai.feignclient;

import com.ByteAndHeartDance.ai.configuration.FeignClientConfig;
import com.ByteAndHeartDance.ai.dto.ProductDTO;
import com.ByteAndHeartDance.ai.dto.ResponsePage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "product-service",
    path = "/v1/product",
    configuration = FeignClientConfig.class,
    url = "http://localhost:8026")
public interface ProductFeignClient {

  @GetMapping("/searchPage")
  ResponsePage<ProductDTO> searchProductsPage(
      @RequestHeader("Authorization") String token,
      @RequestParam String query,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int pageSize);
}
