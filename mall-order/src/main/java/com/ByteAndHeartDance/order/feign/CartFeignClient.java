package com.ByteAndHeartDance.order.feign;

import com.ByteAndHeartDance.entity.RequestPageEntity;

import com.ByteAndHeartDance.order.entity.otherService.feign.CartInfoPage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "mall-shoppingcart-api",
    path = "/v1/shoppingCart"
   )
public interface CartFeignClient {

    @PostMapping("/getShoppingCart")
    CartInfoPage getShoppingCart(@RequestBody RequestPageEntity requestPageEntity);

    @PutMapping("/clearCart")
    void clearCart();

}
