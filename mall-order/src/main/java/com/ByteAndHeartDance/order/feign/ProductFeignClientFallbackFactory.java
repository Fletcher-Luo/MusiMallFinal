package com.ByteAndHeartDance.order.feign;

import com.ByteAndHeartDance.exception.BusinessException;
import com.ByteAndHeartDance.order.entity.otherService.CartProductsSimple;
import com.ByteAndHeartDance.order.entity.otherService.ProductInfo;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ProductFeignClientFallbackFactory implements FallbackFactory<ProductFeignClient> {

  @Override
  public ProductFeignClient create(Throwable cause) {
    return new ProductFeignClient() {
      @Override
      public ProductInfo getProductById(Long productId) {
        // 可以根据需要处理 getProductById 方法的错误情况
        throw new BusinessException("获取产品信息失败：" + cause.getMessage());
      }


      @Override
      public String decreaseStockInAll(List<CartProductsSimple> cartProductsSimpleList) {
        log.info("调用 decreaseStockInAll 方法失败，参数：{}", cartProductsSimpleList);
        if (cause instanceof FeignException.BadRequest) {
          FeignException.BadRequest badRequest = (FeignException.BadRequest) cause;
          String responseBody = new String(badRequest.content());
          log.info("decreaseStockInAll 方法返回错误信息：{}", responseBody);
          if (responseBody.contains("库存不足")) {
            throw new BusinessException(responseBody);
          }
        }
        // 其他错误情况可以根据需要处理
        throw new BusinessException("扣减库存失败：" + cause.getMessage());
      }

      @Override
        public String addStockInAll(List<CartProductsSimple> cartProductsSimpleList) {
            log.info("调用 addStockInAll 方法失败，参数：{}", cartProductsSimpleList);
            throw new BusinessException("增加库存失败：" + cause.getMessage());
        }
    };
  }
}
