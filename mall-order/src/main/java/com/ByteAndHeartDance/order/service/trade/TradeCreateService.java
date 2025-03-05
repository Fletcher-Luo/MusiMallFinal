package com.ByteAndHeartDance.order.service.trade;

import com.ByteAndHeartDance.entity.RequestPageEntity;
import com.ByteAndHeartDance.entity.auth.JwtUserEntity;
import com.ByteAndHeartDance.enums.OrderStatusEnum;
import com.ByteAndHeartDance.exception.BusinessException;
import com.ByteAndHeartDance.helper.IdGenerateHelper;
import com.ByteAndHeartDance.mapper.BaseMapper;
import com.ByteAndHeartDance.order.entity.otherService.CartProductsDetail;
import com.ByteAndHeartDance.order.entity.otherService.feign.CartInfoPage;
import com.ByteAndHeartDance.order.feign.CartFeignClient;
import com.ByteAndHeartDance.order.feign.ProductFeignClient;
import com.ByteAndHeartDance.order.entity.VO.CreateTradeVO;
import com.ByteAndHeartDance.order.entity.order.trade.TradeConditionEntity;
import com.ByteAndHeartDance.order.entity.order.trade.TradeEntity;
import com.ByteAndHeartDance.order.entity.order.tradeItem.TradeItemEntity;
import com.ByteAndHeartDance.order.entity.otherService.CartProductsSimple;
import com.ByteAndHeartDance.order.entity.otherService.ProductInfo;
import com.ByteAndHeartDance.order.enums.PayStatusEnum;
import com.ByteAndHeartDance.order.mapper.order.TradeMapper;
import com.ByteAndHeartDance.order.service.address.DeliveryAddressService;
import com.ByteAndHeartDance.order.utils.ConvertUtil;
import com.ByteAndHeartDance.order.utils.JedisUtil;
import com.ByteAndHeartDance.order.utils.MqOrderDelayMsgUtil;
import com.ByteAndHeartDance.service.BaseService;
import com.ByteAndHeartDance.utils.AssertUtil;
import com.ByteAndHeartDance.utils.FillUserUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/** 交易订单创建 服务层 */
@Slf4j
@Service
public class TradeCreateService extends BaseService<TradeEntity, TradeConditionEntity> {
  private final TradeMapper tradeMapper;
  //  private final RedisUtil redisUtil;
  private final JedisUtil jedisUtil;
  private final TransactionTemplate transactionTemplate;
  private final IdGenerateHelper idGenerateHelper;
  private final RedissonClient redissonClient;
  private final MqOrderDelayMsgUtil mqOrderDelayMsgUtil;
  private final ConvertUtil convertUtil;

  private final DeliveryAddressService deliveryAddressService;
  private final ProductFeignClient productFeignClient;
  private final CartFeignClient cartFeignClient;
  private final TradeItemService tradeItemService;
  private final ExecutorService executorService; // 线程池

  public TradeCreateService(
      TradeMapper tradeMapper,
      JedisUtil jedisUtil,
      TransactionTemplate transactionTemplate,
      IdGenerateHelper idGenerateHelper,
      RedissonClient redissonClient,
      DeliveryAddressService deliveryAddressService,
      MqOrderDelayMsgUtil mqOrderDelayMsgUtil,
      ConvertUtil convertUtil,
      ProductFeignClient productFeignClient,
      CartFeignClient cartFeignClient,
      TradeItemService tradeItemService) {
    this.tradeMapper = tradeMapper;
    //    this.redisUtil = redisUtil;
    this.jedisUtil = jedisUtil;
    this.transactionTemplate = transactionTemplate;
    this.idGenerateHelper = idGenerateHelper;
    this.redissonClient = redissonClient;
    this.deliveryAddressService = deliveryAddressService;
    this.mqOrderDelayMsgUtil = mqOrderDelayMsgUtil;
    this.convertUtil = convertUtil;
    this.productFeignClient = productFeignClient;
    this.cartFeignClient = cartFeignClient;
    this.tradeItemService = tradeItemService;
    this.executorService = Executors.newFixedThreadPool(5); // 创建固定大小的线程池
  }

  private static final String TRADE_ID = "tradeId:"; // 订单ID前缀（存入redis）
  private static final String ORDER_TRADE_LOCK = "orderTradeLock:"; // 订单交易锁前缀
  private static final String PRODUCT_INVENTORY_KEY = "product:inventory:"; // 商品库存的redis库

  @Override
  protected BaseMapper getBaseMapper() {
    return tradeMapper;
  }

  /**
   * 用户购物车下单
   *
   * @param tradeEntity 订单实体
   * @return 订单编号
   */
  public CreateTradeVO createOrderTradeFromCart(TradeEntity tradeEntity) {
    // 获取当前用户信息并校验当前用户是否登录
    JwtUserEntity currentUserInfo = getCurrentUserInfo();
    // 如果订单为空，创建一个新的
    tradeEntity = initTradeEntity(tradeEntity);
    // 生成订单ID或校验订单ID（在redis层面）
    Long tradeId = generateOrValidateTradeId(tradeEntity);
    // 设置订单状态
    setOrderStatusAndAmount(tradeEntity);
    // 从购物车中获取商品ID和购买数量
    List<CartProductsDetail> cartProductsDetail = getGoodsIdAndQuantityFromCart();
    List<CartProductsSimple> cartProductSimples =
        convertUtil.convertCartProductsList(cartProductsDetail);
    // redis校验库存
    checkInventory(cartProductSimples);

    // 从商品服务获取商品详情，并计算订单金额，生成订单项
    List<TradeItemEntity> tradeItemEntities =
        calculateOrderAmountFromCart(tradeEntity, cartProductsDetail, tradeId);
    // 校验送货地址
    validateAndSetDeliveryAddress(currentUserInfo, tradeEntity);
    // 保存订单和相关实体,并扣减库存
    saveOrderAndRelatedEntities(
        currentUserInfo, tradeEntity, tradeId, tradeItemEntities, cartProductSimples);
    // 清空购物车，异步
    cartFeignClient.clearCart();

    // 返回值
    CreateTradeVO createTradeVO = new CreateTradeVO();
    createTradeVO.setTraceNo(tradeId);
    createTradeVO.setTotalAmount(tradeEntity.getPaymentAmount()); // 注：因支付宝字段原因，此处返回需实际支付金额
    createTradeVO.setTotalMoneyOriginal(tradeEntity.getTotalAmount()); // 注：因支付宝字段原因，此处返回订单总金额
    // 设置为：商城订单+订单ID
    createTradeVO.setSubject("商城订单" + tradeId);
    return createTradeVO;
  }

  /**
   * 直接下单
   *
   * @return 订单编号（code/id）
   */
  public CreateTradeVO createOrderTradeDirect(
      TradeEntity tradeEntity, List<CartProductsSimple> cartProductSimples) {
    // 获取当前用户信息并校验当前用户是否登录
    JwtUserEntity currentUserInfo = getCurrentUserInfo();
    // 如果订单为空，创建一个新的
    tradeEntity = initTradeEntity(tradeEntity);
    // 生成订单ID或校验订单ID（在redis层面）
    Long tradeId = generateOrValidateTradeId(tradeEntity);
    // 设置订单状态
    setOrderStatusAndAmount(tradeEntity);
    // redis校验库存
    checkInventory(cartProductSimples);
    // 从商品服务获取商品详情，并计算订单金额，生成订单项
    List<TradeItemEntity> tradeItemEntities =
        calculateOrderAmount(tradeEntity, cartProductSimples, tradeId);
    // 校验送货地址
    validateAndSetDeliveryAddress(currentUserInfo, tradeEntity);
    // 保存订单和相关实体,并扣减库存
    saveOrderAndRelatedEntities(
        currentUserInfo, tradeEntity, tradeId, tradeItemEntities, cartProductSimples);
    // 返回值
    CreateTradeVO createTradeVO = new CreateTradeVO();
    createTradeVO.setTraceNo(tradeId);
    createTradeVO.setTotalAmount(tradeEntity.getPaymentAmount()); // 注：因支付宝字段原因，此处返回需实际支付金额
    createTradeVO.setTotalMoneyOriginal(tradeEntity.getTotalAmount()); // 注：因支付宝字段原因，此处返回订单总金额
    // 设置为：商城订单+订单ID
    createTradeVO.setSubject("商城订单" + tradeId);
    return createTradeVO;
  }

  // 获取当前用户信息并校验当前用户是否登录
  private JwtUserEntity getCurrentUserInfo() {
    JwtUserEntity currentUserInfo = FillUserUtil.getCurrentUserInfo();
    if (Objects.isNull(currentUserInfo)) {
      throw new BusinessException("请先登录");
    }
    return currentUserInfo;
  }

  // 如果订单为空，创建一个新的
  private TradeEntity initTradeEntity(TradeEntity tradeEntity) {
    if (Objects.isNull(tradeEntity)) {
      return new TradeEntity();
    }
    return tradeEntity;
  }

  // 校验送货地址
  private void validateAndSetDeliveryAddress(
      JwtUserEntity currentUserInfo, TradeEntity tradeEntity) {
    if (Objects.isNull(tradeEntity.getDeliveryAddressId())) {
      Long defaultAddressId =
          deliveryAddressService.selectDefaultAddressId(currentUserInfo.getId());
      if (Objects.isNull(defaultAddressId)) {
        throw new BusinessException("您未填入地址也未设置默认地址，请选择地址");
      } else {
        tradeEntity.setDeliveryAddressId(defaultAddressId);
      }
    } else {
      Boolean checkResult =
          deliveryAddressService.checkAddress(
              currentUserInfo.getId(), tradeEntity.getDeliveryAddressId());
      if (!checkResult) {
        throw new BusinessException("地址不存在或不属于您");
      }
    }
  }

  // 生成订单ID或校验订单ID
  private Long generateOrValidateTradeId(TradeEntity tradeEntity) {
    if (Objects.isNull(tradeEntity.getId())) {
      Long tradeId = idGenerateHelper.nextId();
      tradeEntity.setId(tradeId);
      tradeEntity.setCode(String.valueOf(tradeId));
      return tradeId;
    } else {
      // Redis层面校验是否重复下单
      if (!Objects.nonNull(jedisUtil.get(TRADE_ID + tradeEntity.getId()))) {
        throw new BusinessException("请勿重复下单");
      }
      tradeEntity.setCode(String.valueOf(tradeEntity.getId()));
      return tradeEntity.getId();
    }
  }

  // 设置订单状态
  private void setOrderStatusAndAmount(TradeEntity tradeEntity) {
    tradeEntity.setOrderStatus(OrderStatusEnum.CREATE.getValue());
    tradeEntity.setPayStatus(PayStatusEnum.WAIT_PAY.getValue());
    tradeEntity.setOrderTime(new Date());
    // 初始化金额
    tradeEntity.setTotalAmount(BigDecimal.ZERO);
    tradeEntity.setPaymentAmount(BigDecimal.ZERO);
  }

  // 从购物车中获取商品ID和购买数量
  private List<CartProductsDetail> getGoodsIdAndQuantityFromCart() {
    RequestPageEntity requestPageEntity = new RequestPageEntity();
    requestPageEntity.setPageSize(0); // 不分页
    CartInfoPage tmpObjectRet = cartFeignClient.getShoppingCart(requestPageEntity);
    AssertUtil.notNull(tmpObjectRet.getData(), "购物车信息为空");
    AssertUtil.notEmpty(tmpObjectRet.getData(), "购物车信息为空");
    return convertUtil.convertTocartProductsDetailcartProductsDetailList(tmpObjectRet.getData());
  }

  // redis校验库存
  private void checkInventory(List<CartProductsSimple> cartProductsSimple) {
    String script =
        "local insufficientProductId = nil\n"
            + "for i, productId in ipairs(KEYS) do\n"
            + "    local key = '"
            + PRODUCT_INVENTORY_KEY
            + "' .. productId\n"
            + "    local stock = redis.call('get', key)\n"
            + "    if (stock == false or tonumber(stock) < tonumber(ARGV[i])) then\n"
            + "        insufficientProductId = productId\n"
            + "        break\n"
            + "    end\n"
            + "end\n"
            + "if insufficientProductId then\n"
            + "    return insufficientProductId\n"
            + "else\n"
            + "    for i, productId in ipairs(KEYS) do\n"
            + "        local key = '"
            + PRODUCT_INVENTORY_KEY
            + "' .. productId\n"
            + "        redis.call('decrby', key, ARGV[i])\n"
            + "    end\n"
            + "    return 0\n"
            + "end";
    List<String> keys = new ArrayList<>();
    List<String> args = new ArrayList<>();
    for (CartProductsSimple cartProduct : cartProductsSimple) {
      keys.add(String.valueOf(cartProduct.getProductId()));
      args.add(String.valueOf(cartProduct.getQuantity()));
    }

    Object resultObj = jedisUtil.evalInProductDB(script, keys, args);
    log.info("resultObj:{}", resultObj);

    if (resultObj instanceof Long) {
      Long result = (Long) resultObj;
      if (!Objects.equals(result, 0L)) {
        // 库存不足
        throw new BusinessException("商品ID:" + result + "库存不足");
      }
    } else if (resultObj instanceof String) {
      try {
        Long result = Long.parseLong((String) resultObj);
        if (!Objects.equals(result, 0L)) {
          // 库存不足
          throw new BusinessException("商品ID:" + result + "库存不足");
        }
      } catch (NumberFormatException e) {
        throw new BusinessException("无效的返回结果: " + resultObj);
      }
    } else {
      throw new BusinessException("未知的返回结果类型: " + resultObj.getClass().getName());
    }
  }

  // 计算订单金额，并初步生成订单项列表
  private List<TradeItemEntity> calculateOrderAmount(
      TradeEntity tradeEntity, List<CartProductsSimple> cartProductSimples, Long tradeId) {
    List<TradeItemEntity> tradeItemEntities = new ArrayList<>();

    //    for (CartProductsSimple cartProduct : cartProductSimples) {
    //      ProductInfo productInfo = productFeignClient.getProductById(cartProduct.getProductId());
    //      // 1. 是否存在
    //      if (Objects.isNull(productInfo)) {
    //        throw new BusinessException("商品" + cartProduct.getProductId() + "不存在");
    //      }
    //      TradeItemEntity tradeItemEntity = convertUtil.convertToTradeItemEntity(productInfo);
    //      // 设置关联订单ID
    //      tradeItemEntity.setTradeId(tradeId);
    //      tradeItemEntity.setId(idGenerateHelper.nextId());
    //      tradeItemEntity.setQuantity(cartProduct.getQuantity());
    //      // 2. 数量合法
    //      if (cartProduct.getQuantity() <= 0) {
    //        throw new BusinessException("商品数量不合法");
    //      }
    //      // 3. 计算本商品总金额
    //      tradeItemEntity.setAmount(
    //          tradeItemEntity.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity())));
    //      // 4. 累加总金额
    //
    // tradeEntity.setTotalAmount(tradeEntity.getTotalAmount().add(tradeItemEntity.getAmount()));
    //      // 5. 添加订单项
    //      tradeItemEntities.add(tradeItemEntity);
    //    }

    // 并行请求
    List<CompletableFuture<Void>> futures =
        cartProductSimples.stream()
            .map(
                cartProduct ->
                    CompletableFuture.runAsync(
                        () -> {
                          ProductInfo productInfo =
                              productFeignClient.getProductById(cartProduct.getProductId());
                          if (Objects.isNull(productInfo)) {
                            throw new BusinessException("商品" + cartProduct.getProductId() + "不存在");
                          }
                          TradeItemEntity tradeItemEntity =
                              convertUtil.convertToTradeItemEntity(productInfo);
                          tradeItemEntity.setTradeId(tradeId);
                          tradeItemEntity.setId(idGenerateHelper.nextId());
                          tradeItemEntity.setQuantity(cartProduct.getQuantity());
                          if (cartProduct.getQuantity() <= 0) {
                            throw new BusinessException("商品数量不合法");
                          }
                          tradeItemEntity.setAmount(
                              tradeItemEntity
                                  .getPrice()
                                  .multiply(BigDecimal.valueOf(cartProduct.getQuantity())));
                          synchronized (tradeEntity) {
                            tradeEntity.setTotalAmount(
                                tradeEntity.getTotalAmount().add(tradeItemEntity.getAmount()));
                          }
                          tradeItemEntities.add(tradeItemEntity);
                        }))
            .collect(Collectors.toList());

    // 等待所有异步任务完成
    try {
      CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
    } catch (InterruptedException | ExecutionException e) {
      // 处理异常
      if (e.getCause() instanceof BusinessException) {
        throw (BusinessException) e.getCause();
      } else {
        throw new RuntimeException(e.getCause());
      }
    }
    // LAST. 设置订单支付金额
    tradeEntity.setPaymentAmount(tradeEntity.getTotalAmount());
    return tradeItemEntities;
  }

  /**
   * 计算订单金额，并初步生成订单项列表（来自购物车）
   *
   * @param tradeEntity 订单实体
   * @param cartProductsDetail 购物车商品详情
   * @param tradeId 订单ID
   * @return 订单项列表 由于来自购物车，购物车服务已经自动请求了商品服务，所以不需要再次请求商品服务
   */
  private List<TradeItemEntity> calculateOrderAmountFromCart(
      TradeEntity tradeEntity, List<CartProductsDetail> cartProductsDetail, Long tradeId) {
    List<TradeItemEntity> tradeItemEntities = new ArrayList<>();
    for (CartProductsDetail cartProduct : cartProductsDetail) {
      TradeItemEntity tradeItemEntity = new TradeItemEntity();
      tradeItemEntity.setProductId(cartProduct.getProductId());
      tradeItemEntity.setQuantity(cartProduct.getQuantity());
      tradeItemEntity.setTradeId(tradeId);
      tradeItemEntity.setId(idGenerateHelper.nextId());

      tradeItemEntity.setPicture(cartProduct.getCover());
      tradeItemEntity.setProductId(cartProduct.getProductId());
      tradeItemEntity.setProductName(cartProduct.getProductName());
      tradeItemEntity.setPrice(cartProduct.getPrice());
      tradeItemEntity.setAmount(
          cartProduct.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity())));
      tradeItemEntity.setModel(cartProduct.getModel());
      tradeItemEntities.add(tradeItemEntity);
      // 累加总金额
      tradeEntity.setTotalAmount(tradeEntity.getTotalAmount().add(tradeItemEntity.getAmount()));
    }
    tradeEntity.setPaymentAmount(tradeEntity.getTotalAmount());
    return tradeItemEntities;
  }

  // 扣减库存
  private void decreaseStock(List<CartProductsSimple> cartProductSimples) {
    String decreaseStockResult = productFeignClient.decreaseStockInAll(cartProductSimples);
    if (Objects.equals(decreaseStockResult, "库存扣减成功")) {
      return;
    } else {
      throw new BusinessException("库存扣减失败:" + decreaseStockResult);
    }
  }

  // 保存订单和相关实体
  private void saveOrderAndRelatedEntities(
      JwtUserEntity currentUserInfo,
      TradeEntity tradeEntity,
      Long tradeId,
      List<TradeItemEntity> tradeItemEntities,
      List<CartProductsSimple> cartProductSimples) {
    tradeEntity.setUserId(currentUserInfo.getId());
    tradeEntity.setUserName(currentUserInfo.getUsername());
    RLock lock = redissonClient.getLock(ORDER_TRADE_LOCK + tradeId);
    try {
      if (lock.tryLock(10, 60, TimeUnit.SECONDS)) {
        if (Objects.nonNull(tradeMapper.findById(tradeId))) {
          throw new BusinessException("请勿重复下单");
        }
        // 扣减库存
        decreaseStock(cartProductSimples);
        transactionTemplate.execute(
            status -> {
              tradeMapper.insert(tradeEntity);
              // 保存订单项
              tradeItemService.batchInsert(tradeItemEntities);
              // 从redis中删除订单ID，以免重复下单
              jedisUtil.del(TRADE_ID + tradeId);
              return null;
            });
        // 发送延迟消息（订单超时自动取消）
        mqOrderDelayMsgUtil.sendOrderDelayMsg(tradeId);
      }
    } catch (InterruptedException e) {
      log.error("获取分布式锁失败", e);
      throw new BusinessException("订单创建失败：疑似重复下单");
    } finally {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }
  }
}
