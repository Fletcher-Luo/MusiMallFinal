package com.mall.product_service.controller;

import com.ByteAndHeartDance.utils.AssertUtil;
import com.mall.product_service.service.InventorySyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 商品库存同步服务 Controller
 *
 * <p>功能： 1. 商品库存同步到redis服务
 */
@RestController
@RequestMapping("/v1/inventorySync")
@Tag(name = "商品库存同步服务", description = "商品库存同步操作接口")
public class InventorySyncController {

  private final InventorySyncService inventorySyncService;

  // 构造方法注入 ProductService
  public InventorySyncController(InventorySyncService inventorySyncService) {
    this.inventorySyncService = inventorySyncService;
  }

    /**
     * 同步商品库存信息到 Redis
     *
     * @return 同步结果
     */
    @Operation(summary = "同步全部商品库存信息到 Redis")
    @PostMapping("/syncAll")
    public ResponseEntity<String> syncInventoryToRedis() {
        inventorySyncService.syncInventoryToRedis();
        return ResponseEntity.ok("全量商品库存信息同步任务已提交");
    }

    /**
     * 同步指定商品库存信息到 Redis
     *
     * @param productId 商品 ID
     * @return 同步结果
     */
    @Operation(summary = "同步指定商品库存信息到 Redis")
    @PostMapping("/sync/{productId}")
    public ResponseEntity<String> syncInventoryToRedis(@PathVariable Long productId) {
        AssertUtil.notNull(productId, "商品 ID 不能为空");
        inventorySyncService.syncInventoryToRedis(productId);
        return ResponseEntity.ok("商品库存信息同步完成");
    }

    /**
     * redis中删除全部商品库存信息
     */
    @Operation(summary = "Redis删除全部商品库存信息")
    @DeleteMapping("/deleteAllInventoryRedis")
    public ResponseEntity<String> deleteAllInventoryRedis() {
        inventorySyncService.deleteAllInventoryRedis();
        return ResponseEntity.ok("在redis中删除全部商品库存信息成功");
    }

}
