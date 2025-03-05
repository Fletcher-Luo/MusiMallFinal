package com.ByteAndHeartDance.shoppingcart.feign;


import com.ByteAndHeartDance.shoppingcart.entity.feignProduct.ProductDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// name 是调用rest接口对应的服务名称 path 是调用rest接口所带的ProductController指定的requestMapping路径
@FeignClient(name="product-service",path = "/v1/product")
public interface ProductFeignService {
   /**
    * 获取单个商品详情
    *
    * @param id 商品 ID
    * @return 商品详情 DTO
    */
   @GetMapping("/{id}")
   @Operation(summary = "根据ID查询商品", description = "根据ID查询商品")
   ResponseEntity<ProductDTO> getProductById(@PathVariable Long id);
}