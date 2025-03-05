package com.mall.product_service.feign;

import com.mall.product_service.entity.dto.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "product-service") // 商品服务的地址
public interface ProductFeignClient {

    @GetMapping("/products")
    List<Product> getProducts(@RequestParam int page,
                              @RequestParam int pageSize,
                              @RequestParam(required = false) String categoryName);

    @GetMapping("/products/{id}")
    Product getProductById(@PathVariable("id") Long id);

    @GetMapping("/products/search")
    List<Product> searchProducts(@RequestParam String query);
}