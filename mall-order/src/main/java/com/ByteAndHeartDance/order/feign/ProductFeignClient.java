package com.ByteAndHeartDance.order.feign;

import com.ByteAndHeartDance.order.entity.otherService.CartProductsSimple;
import com.ByteAndHeartDance.order.entity.otherService.ProductInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(
    name = "product-service",
    path = "/v1/product",
    fallbackFactory =
        com.ByteAndHeartDance.order
            .feign
            .ProductFeignClientFallbackFactory
            .class, url = "http://localhost:8026") // url = "http://localhost:8026"
public interface ProductFeignClient {

  @GetMapping("/{productId}")
  ProductInfo getProductById(@PathVariable("productId") Long productId);

  @PostMapping("/decreaseStockInAll")
  String decreaseStockInAll(List<CartProductsSimple> cartProductsSimpleList);

  @PostMapping("/addStockInAll")
  String addStockInAll(List<CartProductsSimple> cartProductsSimpleList);
}
