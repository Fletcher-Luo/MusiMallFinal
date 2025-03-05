package com.mall.product_service.service;

import com.ByteAndHeartDance.utils.AssertUtil;
import com.mall.product_service.entity.dto.ProductDTO;
import com.mall.product_service.utils.JedisUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** InventorySyncService：商品库存同步服务 1. 从商品服务获取商品库存信息 2. 将商品库存信息同步到redis数据库 */
@Service
@Slf4j
public class InventorySyncService {

  private final ProductService productService;
  private final JedisUtil jedisUtil;
  private final ExecutorService executorService; // 线程池

  public InventorySyncService(ProductService productService, JedisUtil jedisUtil ) {
    this.productService = productService;
    this.jedisUtil = jedisUtil;
    this.executorService =  Executors.newFixedThreadPool(2); // 创建固定大小的线程池
  }

  private static final String PRODUCT_INVENTORY_KEY = "product:inventory:";

  /** 同步商品库存信息 */
  public void syncInventoryToRedis() {
    deleteAllInventoryRedis();
    log.info("开始全量同步商品库存信息");
    // 异步执行
    executorService.submit(() -> {
      jedisUtil.init(); // 初始化Jedis连接池
      productService
              .getProducts(-1, 100, null).getData()
              .forEach(
                      productDTO -> {
                        // 将商品库存信息同步到redis数据库
                        jedisUtil.set(
                                PRODUCT_INVENTORY_KEY + productDTO.getId(),
                                String.valueOf(productDTO.getRemainQuantity()));
                      });
      log.info("商品库存信息全量同步完成");
    });
  }

  /**
   * 同步指定商品库存信息
   *
   * @param productId 商品 ID
   */
  public void syncInventoryToRedis(Long productId) {

    ProductDTO productDTO = productService.getProductById(productId);
    AssertUtil.notNull(productDTO, "商品不存在");
    jedisUtil.init(); // 初始化Jedis连接池
    jedisUtil.set(
        PRODUCT_INVENTORY_KEY + productDTO.getId(), String.valueOf(productDTO.getRemainQuantity()));
  }

  /** 在redis中删除库存 */
  public void deleteAllInventoryRedis() {
    jedisUtil.init();
    jedisUtil.delKeysWithPrefix(PRODUCT_INVENTORY_KEY);
  }
}
