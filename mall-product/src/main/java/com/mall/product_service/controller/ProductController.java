package com.mall.product_service.controller;

import com.ByteAndHeartDance.entity.ResponsePageEntity;
import com.ByteAndHeartDance.utils.AssertUtil;
import com.mall.product_service.entity.dto.ProductDTO;
import com.mall.product_service.entity.dto.ProductsDecreaseInfoDTO;
import com.mall.product_service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品服务 Controller
 *
 * <p>功能： 1. 获取商品列表（支持分页、按类别筛选） 2. 获取单个商品详情 3. 通过关键字搜索商品 4. 创建商品（可选） 5. 修改商品信息（可选） 6. 删除商品（可选） 7.
 * 扣减库存 8. 增加库存 9. 查询商品剩余库存 10. 批量扣减库存
 */
@RestController
@RequestMapping("/v1/product")
@Tag(name = "商品操作", description = "商品操作接口")
public class ProductController {

  private final ProductService productService;
  private final TransactionTemplate transactionTemplate; // 用于并发事务

  // 构造方法注入 ProductService
  public ProductController(ProductService productService, TransactionTemplate transactionTemplate) {
    this.productService = productService;
    this.transactionTemplate = transactionTemplate;
  }

  /**
   * 获取商品列表（支持分页和按类别筛选）
   *
   * @param page 页码，默认从 1 开始
   * @param pageSize 每页显示的数量，默认 10
   * @param categoryName 商品类别（可选）
   * @return 商品列表 DTO
   */
  @GetMapping
  @Operation(summary = "查询当前商品列表", description = "查询当前商品列表 页码为-1表示所有")
  public ResponsePageEntity<ProductDTO> listProducts(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(required = false) String categoryName) {
    return productService.getProducts(page, pageSize, categoryName);
  }

  /**
   * 获取单个商品详情
   *
   * @param id 商品 ID
   * @return 商品详情 DTO
   */
  @GetMapping("/{id}")
  @Operation(summary = "根据ID查询商品", description = "根据ID查询商品")
  public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
    ProductDTO productDTO = productService.getProductById(id);
    return productDTO != null ? ResponseEntity.ok(productDTO) : ResponseEntity.notFound().build();
  }

  /**
   * 通过关键字搜索商品
   *
   * @param query 搜索关键字
   * @return 搜索结果列表 DTO
   */
  @GetMapping("/search")
  @Operation(summary = "搜索商品", description = "通过关键字搜索商品")
  public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String query) {
    List<ProductDTO> resultList = productService.searchProducts(query);
    return ResponseEntity.ok(resultList);
  }

  /**
   * 通过关键字分页搜索商品
   *
   * @param query 搜索关键字
   * @param page 页码，默认从 0 开始
   * @param pageSize 每页显示的数量，默认 10
   */
  @GetMapping("/searchPage")
  @Operation(summary = "分页搜索商品", description = "通过关键字分页搜索商品")
  public ResponsePageEntity<ProductDTO> searchProductsPage(
      @RequestParam String query,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int pageSize) {
    return productService.searchProductsPage(query, page, pageSize);
  }

  // 以下为管理接口（创建、更新、删除商品），根据实际需求决定是否开放

  /**
   * 创建新商品
   *
   * @param productDTO 新商品信息 DTO
   * @return 创建成功后的商品信息 DTO
   */
  @PostMapping
  @Operation(summary = "创建商品", description = "创建商品")
  public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
    ProductDTO createdProduct = productService.createProduct(productDTO);
    return ResponseEntity.ok(createdProduct);
  }

  /**
   * 修改商品信息
   *
   * @param id 商品 ID
   * @param productDTO 修改后的商品信息 DTO
   * @return 修改成功后的商品信息 DTO
   */
  @PutMapping("/{id}")
  @Operation(summary = "根据ID修改商品", description = "修改商品")
  public ResponseEntity<ProductDTO> updateProduct(
      @PathVariable Long id, @RequestBody ProductDTO productDTO) {
    AssertUtil.notNull(id, "商品 ID 不能为空");
    AssertUtil.notNull(productDTO, "商品信息不能为空");
    ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
    return ResponseEntity.ok(updatedProduct);
  }

  /**
   * 删除商品
   *
   * @param id 商品 ID
   * @return 无内容（204）
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "根据ID删除商品", description = "删除商品")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    AssertUtil.notNull(id, "商品 ID 不能为空");
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();
  }

  // === 以下是库存管理的接口 ===

  /**
   * 扣减库存接口
   *
   * @param productId 商品ID
   * @param amount 扣减的数量
   * @return 是否成功
   */
  @PostMapping("/{productId}/reduceStock")
  @Operation(summary = "扣减库存", description = "扣减库存")
  public ResponseEntity<String> reduceStock(
      @PathVariable Long productId, @RequestParam Integer amount) {
    boolean success = productService.reduceStock(productId, amount);
    return success ? ResponseEntity.ok("库存扣减成功") : ResponseEntity.badRequest().body("库存不足或操作失败");
  }

  /**
   * 扣减库存接口（批量）
   *
   * @param cartProductsList 购物车商品列表
   * @return 是否成功
   */
  @PostMapping("/decreaseStockInAll")
  @Operation(summary = "批量扣减库存", description = "批量扣减库存")
  public ResponseEntity<String> decreaseStockInAll(
      @RequestBody List<ProductsDecreaseInfoDTO> cartProductsList) {
    String result =
        transactionTemplate.execute(
            status -> {
              for (ProductsDecreaseInfoDTO productsDecreaseInfoDTO : cartProductsList) {
                boolean flag =
                    productService.reduceStock(
                        productsDecreaseInfoDTO.getProductId(),
                        productsDecreaseInfoDTO.getQuantity());
                if (!flag) {
                  // 如果有一个商品扣减库存失败，则回滚事务
                  status.setRollbackOnly();
                  // 查询商品名称
                  try {
                    ProductDTO productDTO =
                        productService.getProductById(productsDecreaseInfoDTO.getProductId());
                    return "商品" + productDTO.getName() + "库存不足";
                  } catch (Exception e) {
                    return "商品ID：" + productsDecreaseInfoDTO.getProductId() + "库存不足";
                  }
                }
              }
              return "成功";
            });
    if (result != null) {
      return result.equals("成功")
          ? ResponseEntity.ok("库存扣减成功")
          : ResponseEntity.badRequest().body(result);
    }
    return ResponseEntity.badRequest().body("库存扣减失败");
  }

  /**
   * 增加库存接口（批量）
   *
   * @param cartProductsList 购物车商品列表
   * @return 是否成功
   */
  @PostMapping("/addStockInAll")
  @Operation(summary = "批量增加库存", description = "批量增加库存")
  public ResponseEntity<String> addStockInAll(
      @RequestBody List<ProductsDecreaseInfoDTO> cartProductsList) {
    String result =
        transactionTemplate.execute(
            status -> {
              for (ProductsDecreaseInfoDTO productsDecreaseInfoDTO : cartProductsList) {
                boolean flag =
                    productService.addStock(
                        productsDecreaseInfoDTO.getProductId(),
                        productsDecreaseInfoDTO.getQuantity());
                if (!flag) {
                  // 如果有一个商品增加库存失败，则回滚事务
                  status.setRollbackOnly();
                  // 查询商品名称
                  try {
                    ProductDTO productDTO =
                        productService.getProductById(productsDecreaseInfoDTO.getProductId());
                    return "商品" + productDTO.getName() + "库存增加失败";
                  } catch (Exception e) {
                    return "商品ID：" + productsDecreaseInfoDTO.getProductId() + "库存增加失败";
                  }
                }
              }
              return "成功";
            });
    if (result != null) {
      return result.equals("成功")
          ? ResponseEntity.ok("库存增加成功")
          : ResponseEntity.badRequest().body(result);
    }
    return ResponseEntity.badRequest().body("库存增加失败");
  }

  /**
   * 增加库存接口
   *
   * @param productId 商品ID
   * @param amount 增加的数量
   * @return 是否成功
   */
  @PostMapping("/{productId}/addStock")
  @Operation(summary = "增加库存", description = "增加库存")
  public ResponseEntity<String> addStock(
      @PathVariable Long productId, @RequestParam Integer amount) {
    boolean success = productService.addStock(productId, amount);
    return success ? ResponseEntity.ok("库存增加成功") : ResponseEntity.badRequest().body("库存增加失败");
  }

  /**
   * 查询商品剩余库存接口
   *
   * @param productId 商品ID
   * @return 剩余库存数量
   */
  @GetMapping("/{productId}/remainStock")
  @Operation(summary = "查询商品剩余库存", description = "查询商品剩余库存")
  public ResponseEntity<Integer> getRemainStock(@PathVariable Long productId) {
    Integer remainingStock = productService.getRemainStock(productId);
    return remainingStock != null
        ? ResponseEntity.ok(remainingStock)
        : ResponseEntity.notFound().build();
  }
}
