package com.ByteAndHeartDance.shoppingcart.helper;

import static com.ByteAndHeartDance.utils.FillUserUtil.getCurrentUserInfo;

import com.ByteAndHeartDance.entity.auth.JwtUserEntity;
import com.ByteAndHeartDance.shoppingcart.entity.feignProduct.ProductDTO;
import com.ByteAndHeartDance.shoppingcart.entity.userproduct.UserProductEntity;
import com.ByteAndHeartDance.shoppingcart.feign.ProductFeignService;
import com.ByteAndHeartDance.shoppingcart.util.JedisUtil;
import com.ByteAndHeartDance.utils.AssertUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @Title UserProductHelper @Description 用户商品辅助类 @Author y @CreateTime 2025/02/13 19:25 @Version
 * 1.0.0
 */
@Component
@Slf4j
public class UserProductHelper {
  // 只在查询时用到，避免同一件商品重复调用远程接口进行查询校验
  // x小时
  private static final long EXPIRE_TIME = 60 * 60 * 2;
  private static final String PRODUCT_KEY = "shoppingCart:product:id_";
  private final JedisUtil jedisUtil;
  private final ProductFeignService productFeignService;
  private final ExecutorService executorService;

  @Autowired
  public UserProductHelper(JedisUtil jedisUtil, ProductFeignService productFeignService) {
    this.jedisUtil = jedisUtil;
    this.productFeignService = productFeignService;
    //
    executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
  }

  /**
   * 校验参数
   *
   * @param userProductEntity 用户商品实体 包含商品id @Description 获取用户商品实体中的商品id，查询商品信息，确保商品存在
   */
  public void checkParam(UserProductEntity userProductEntity) {
    // 当前用户信息
    JwtUserEntity currentUserInfo = getCurrentUserInfo();
    // 获取商品id
    Long productId = userProductEntity.getProductId();
    // 先检查缓存，若存在，直接校验成功
    //        Map<String, String> product = jedisUtil.hgetall(PRODUCT_KEY + productId);
    String pid = jedisUtil.get(PRODUCT_KEY + productId);
    // 缓存中不存在
    if (Objects.isNull(pid)) {
      // 获取到Product对象
      ProductDTO productEntity = productFeignService.getProductById(productId).getBody();
      // *用商品模块远程调用结果是否存在校验商品是否存在
      AssertUtil.notNull(productEntity, "该商品在系统中不存在");
      // 校验成功加入缓存
      jedisUtil.setWithExpire(PRODUCT_KEY + productId, productEntity.getId() + "", EXPIRE_TIME);
    }
    // 确认商品存在后，添加用户id到用户商品记录实体中
    userProductEntity.setUserId(currentUserInfo.getId());
  }

  /**
   * 填充用户和商品信息
   *
   * @param list 用户商品实体集合
   */
  public void fillUserProductInfo(List<? extends UserProductEntity> list) {
    fillUserInfo(list);
    fillProductInfo(list);
  }

  /**
   * 填充用户信息
   *
   * @param list 用户集合
   */
  public void fillUserInfo(List<? extends UserProductEntity> list) {
    if (CollectionUtils.isEmpty(list)) {
      return;
    }
    // 当前用户信息
    JwtUserEntity currentUserInfo = getCurrentUserInfo();
    for (UserProductEntity userProductEntity : list) {
      userProductEntity.setUserName(currentUserInfo.getUsername());
    }
  }

  public void fillUserInfo(UserProductEntity userProductEntity) {
    if (Objects.isNull(userProductEntity)) {
      return;
    }
    // 当前用户信息
    JwtUserEntity currentUserInfo = getCurrentUserInfo();
    userProductEntity.setUserName(currentUserInfo.getUsername());
  }

  /**
   * 填充商品信息
   *
   * @param list 用户商品实体集合
   */
  public void fillProductInfo(List<? extends UserProductEntity> list) {
    if (CollectionUtils.isEmpty(list)) {
      return;
    }
    // 获取商品id集合
    List<Long> productIdList =
        list.stream().map(UserProductEntity::getProductId).distinct().collect(Collectors.toList());
    // 并行获取商品信息
    List<ProductDTO> productDTOList = getProductsByIds(productIdList);

    // 使用Map存储商品信息，方便快速查找
    Map<Long, ProductDTO> productDTOMap =
        productDTOList.stream()
            .collect(Collectors.toMap(ProductDTO::getId, productDTO -> productDTO));

    for (UserProductEntity userProductEntity : list) {
      // 获取商品信息
      ProductDTO productDTO = productDTOMap.get(userProductEntity.getProductId());
      if (productDTO != null) {
        // 填充商品信息
        userProductEntity.setModel(productDTO.getDescription());
        userProductEntity.setProductName(productDTO.getName());
        userProductEntity.setPrice(productDTO.getPrice());
        userProductEntity.setStock(productDTO.getQuantity());
        userProductEntity.setCover(productDTO.getPicture());
      }
    }

  }

  public List<ProductDTO> getProductsByIds(List<Long> productIds) {
    List<Future<ProductDTO>> futures = new ArrayList<>();
    List<ProductDTO> productDTOList = new ArrayList<>();
    // 并行调用getProductById接口
    for (Long productId : productIds) {
      futures.add(
          executorService.submit(
              () -> {
                try {
                  ResponseEntity<ProductDTO> response =
                      productFeignService.getProductById(productId);
                  return response.getBody();
                } catch (Exception e) {
                  log.error("Error occurred while fetching product with ID: " + productId, e);
                  throw e;
                }
              }));
    }
    // 收集结果
    for (Future<ProductDTO> future : futures) {
      try {
        ProductDTO productDTO = future.get();
        productDTOList.add(productDTO);
      } catch (InterruptedException | ExecutionException e) {
        log.error("Error occurred while fetching product", e);
        throw new RuntimeException(e);
      }
    }
    return productDTOList;
  }

  // 关闭线程池
  @PreDestroy
  public void shutdown() {
    executorService.shutdown();
    try {
      if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
        executorService.shutdownNow();
      }
    } catch (InterruptedException e) {
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
