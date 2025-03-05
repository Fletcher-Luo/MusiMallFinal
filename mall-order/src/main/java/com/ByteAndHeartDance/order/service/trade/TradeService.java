package com.ByteAndHeartDance.order.service.trade;

import com.ByteAndHeartDance.entity.ResponsePageEntity;
import com.ByteAndHeartDance.entity.auth.JwtUserEntity;
import com.ByteAndHeartDance.enums.OrderStatusEnum;
import com.ByteAndHeartDance.exception.BusinessException;
import com.ByteAndHeartDance.helper.IdGenerateHelper;
import com.ByteAndHeartDance.mapper.BaseMapper;
import com.ByteAndHeartDance.order.config.MqConfig;
import com.ByteAndHeartDance.order.entity.VO.TradeEntityAndItemVO;
import com.ByteAndHeartDance.order.entity.VO.TradeItemVO;
import com.ByteAndHeartDance.order.entity.order.trade.TradeConditionEntity;
import com.ByteAndHeartDance.order.entity.order.trade.TradeEntity;
import com.ByteAndHeartDance.order.entity.order.tradeItem.TradeItemEntity;
import com.ByteAndHeartDance.order.entity.otherService.CartProductsSimple;
import com.ByteAndHeartDance.order.enums.PayStatusEnum;
import com.ByteAndHeartDance.order.enums.PayTypeEnum;
import com.ByteAndHeartDance.order.feign.ProductFeignClient;
import com.ByteAndHeartDance.order.mapper.order.TradeMapper;
import com.ByteAndHeartDance.order.utils.JedisUtil;
import com.ByteAndHeartDance.service.BaseService;
import com.ByteAndHeartDance.utils.AssertUtil;
import com.ByteAndHeartDance.utils.FillUserUtil;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** 交易订单查询、新增、删除 服务层 */
@Slf4j
@Service
public class TradeService extends BaseService<TradeEntity, TradeConditionEntity> {
  private final TradeMapper tradeMapper;
  private final TradeItemService tradeItemService;
  private final JedisUtil jedisUtil;
  private final IdGenerateHelper idGenerateHelper;
  private final ProductFeignClient productFeignClient;

  public TradeService(
      TradeMapper tradeMapper,
      JedisUtil jedisUtil,
      IdGenerateHelper idGenerateHelper,
      ProductFeignClient productFeignClient,
      TradeItemService tradeItemService) {
    this.tradeMapper = tradeMapper;
    //    this.redisUtil = redisUtil;
    this.jedisUtil = jedisUtil;
    this.idGenerateHelper = idGenerateHelper;
    this.tradeItemService = tradeItemService;
    this.productFeignClient = productFeignClient;
  }

  private static final String TRADE_ID = "tradeId:"; // 订单ID前缀（存入redis）
  private static final Integer TRADE_ID_PRE_CREATE_EXPIRE_TIME = 15 * 60; // 订单ID预分配过期时间（15分钟）

  @Override
  protected BaseMapper getBaseMapper() {
    return tradeMapper;
  }

  /**
   * 根据条件分页查询订单列表（分页，直接调用）
   *
   * @param tradeConditionEntity 订单信息
   * @return 订单集合
   */
  public ResponsePageEntity<TradeEntityAndItemVO> searchByPage(
      TradeConditionEntity tradeConditionEntity) {
    // 如果查询条件为空，创建一个新的
    if (Objects.isNull(tradeConditionEntity)) {
      tradeConditionEntity = new TradeConditionEntity();
    }
    ResponsePageEntity<TradeEntity> tradeRes =
        super.searchByPage(tradeConditionEntity); // 直接调用父类的分页查询
    // 获取List
    List<TradeEntity> tradeEntities = tradeRes.getData();
    // 查找每个订单具体的TradeItem
    List<Long> tradeIds = tradeEntities.stream().map(TradeEntity::getId).toList();
    if (tradeIds.isEmpty()) {
      return new ResponsePageEntity<>(
          tradeRes.getPageNo(),
          tradeRes.getPageSize(),
          tradeRes.getTotalPage(),
          tradeRes.getTotalCount(),
          new ArrayList<>());
    }
    List<TradeItemEntity> tradeItemEntities = tradeItemService.findByTradeIds(tradeIds);

    List<TradeEntityAndItemVO> ret = new ArrayList<>();
    // 将TradeItemEntity加入TradeEntity
    for (TradeEntity tradeEntity : tradeEntities) {
      TradeEntityAndItemVO tradeEntityAndItemVO = new TradeEntityAndItemVO();

      // 拷贝TradeEntity到TradeEntityAndItemVO
      BeanUtils.copyProperties(tradeEntity, tradeEntityAndItemVO);

      List<TradeItemVO> tradeItemVOList =
          tradeItemEntities.stream()
              .filter(tradeItemEntity -> tradeItemEntity.getTradeId().equals(tradeEntity.getId()))
              .map(
                  tradeItemEntity -> {
                    TradeItemVO tradeItemVO = new TradeItemVO();
                    BeanUtils.copyProperties(tradeItemEntity, tradeItemVO);
                    return tradeItemVO;
                  })
              .toList();

      tradeEntityAndItemVO.setTradeItemList(tradeItemVOList);
      ret.add(tradeEntityAndItemVO);
    }
    return new ResponsePageEntity<>(
        tradeRes.getPageNo(),
        tradeRes.getPageSize(),
        tradeRes.getTotalPage(),
        tradeRes.getTotalCount(),
        ret);
  }

  /**
   * 根据条件查询当前用户的订单列表
   *
   * @param tradeConditionEntity 条件
   * @return 订单列表
   */
  public ResponsePageEntity<TradeEntityAndItemVO> searchByPageCurrentUser(
      TradeConditionEntity tradeConditionEntity) {
    // 获取当前用户信息
    JwtUserEntity currentUserInfo = FillUserUtil.getCurrentUserInfo();
    AssertUtil.notNull(currentUserInfo, "请先登录");
    // 如果查询条件为空，创建一个新的
    if (Objects.isNull(tradeConditionEntity)) {
      tradeConditionEntity = new TradeConditionEntity();
    }
    // 设置当前用户ID
    tradeConditionEntity.setUserId(currentUserInfo.getId());
    // 调用分页查询
    return searchByPage(tradeConditionEntity);
  }

  /**
   * 根据条件查询订单列表（不分页）
   *
   * @param tradeConditionEntity 查询条件
   * @return 订单集合
   */
  public List<TradeEntity> searchByCondition(TradeConditionEntity tradeConditionEntity) {
    tradeConditionEntity.setPageSize(0); // 不分页
    return tradeMapper.searchByCondition(tradeConditionEntity);
  }

  /**
   * 根据交易订单ID查询交易订单
   *
   * @param id 订单ID
   * @return 订单信息
   */
  public TradeEntityAndItemVO findById(Long id) {
    TradeEntity tradeEntity = tradeMapper.findById(id);
    AssertUtil.notNull(tradeEntity, "订单不存在或已被删除");
    // 查找订单的TradeItem
    List<TradeItemEntity> tradeItemEntities = tradeItemService.findByTradeId(id);
    TradeEntityAndItemVO tradeEntityAndItemVO = new TradeEntityAndItemVO();
    BeanUtils.copyProperties(tradeEntity, tradeEntityAndItemVO);
    List<TradeItemVO> tradeItemVOList =
        tradeItemEntities.stream()
            .map(
                tradeItemEntity -> {
                  TradeItemVO tradeItemVO = new TradeItemVO();
                  BeanUtils.copyProperties(tradeItemEntity, tradeItemVO);
                  return tradeItemVO;
                })
            .toList();
    tradeEntityAndItemVO.setTradeItemList(tradeItemVOList);
    return tradeEntityAndItemVO;
  }

  /**
   * 根据订单编码查询订单
   *
   * @param code 订单编码
   * @return 订单集合
   */
  public TradeEntity findByCode(String code) {
    TradeConditionEntity tradeConditionEntity = new TradeConditionEntity();
    tradeConditionEntity.setCode(code);
    List<TradeEntity> tradeEntities = tradeMapper.searchByCondition(tradeConditionEntity);
    if (CollectionUtils.isEmpty(tradeEntities)) {
      return null;
    }
    return tradeEntities.get(0);
  }

  /**
   * 根据条件查询订单数量
   *
   * @param tradeConditionEntity 查询条件
   * @return 数量
   */
  public int searchCount(TradeConditionEntity tradeConditionEntity) {
    return tradeMapper.searchCount(tradeConditionEntity);
  }

  /**
   * 修改订单
   *
   * @param tradeEntity 订单信息
   * @return 结果
   */
  public int update(TradeEntity tradeEntity) {
    AssertUtil.notNull(tradeEntity, "订单信息不能为空");
    AssertUtil.notNull(tradeEntity.getId(), "订单ID不能为空");
    return tradeMapper.update(tradeEntity);
  }

  /**
   * 批量删除订单对象
   *
   * @param ids 系统ID集合
   * @return 结果
   */
  public int deleteByIds(List<Long> ids) {
    List<TradeEntity> entities = tradeMapper.findByIds(ids);
    AssertUtil.notEmpty(entities, "订单不存在或已被删除");

    TradeEntity entity = new TradeEntity();
    FillUserUtil.fillUpdateUserInfo(entity);
    return tradeMapper.deleteByIds(ids, entity);
  }

  /**
   * 预分配订单ID
   *
   * @return 订单ID
   */
  public Long preCreateTradeId() {
    Long tradeId = idGenerateHelper.nextId();
    // 将订单ID存入redis，过期时间15分钟
    jedisUtil.set(TRADE_ID + tradeId, String.valueOf(tradeId), TRADE_ID_PRE_CREATE_EXPIRE_TIME);
    return tradeId;
  }

  /**
   * 标记订单已支付
   *
   * @param id 订单ID
   * @param alipayTraceNo 支付宝交易号
   * @return 结果
   */
  public int markOrderPaid(@NotNull Long id, @NotNull String alipayTraceNo, Integer payType) {
    TradeEntity tradeEntity = tradeMapper.findById(id);
    AssertUtil.notNull(tradeEntity, "订单不存在");
    //    AssertUtil.notNull(alipayTraceNo, "支付宝交易号不能为空");
    // 校验订单状态
    if (!OrderStatusEnum.CREATE.getValue().equals(tradeEntity.getOrderStatus())) {
      throw new BusinessException("订单不处于待支付状态");
    }
    if (!PayStatusEnum.WAIT_PAY.getValue().equals(tradeEntity.getPayStatus())) {
      throw new BusinessException("订单支付状态不处于待支付状态");
    }
    // 修改订单状态为已支付
    TradeEntity updateEntity = new TradeEntity();
    updateEntity.setId(id);
    updateEntity.setOrderStatus(OrderStatusEnum.PAY.getValue());
    updateEntity.setPayStatus(PayStatusEnum.PAYMENT.getValue());
    updateEntity.setPayType(payType); // 支付方式
    //    FillUserUtil.fillUpdateUserInfo(updateEntity);
    if (alipayTraceNo != null) updateEntity.setAlipayTraceNo(alipayTraceNo);
    // 删除redis中的订单ID
    jedisUtil.del(
        OrderPayFinishByAlipayNotificationService.KEY_NAME + id,
        OrderPayFinishByAlipayNotificationService.PAY_REDIS_DATABASE_INDEX);
    return tradeMapper.update(updateEntity);
  }

  /**
   * 标记订单已完成
   *
   * @param id 订单ID
   */
  public int markOrderFinished(@NotNull Long id) {
    TradeEntity tradeEntity = tradeMapper.findById(id);
    AssertUtil.notNull(tradeEntity, "订单不存在");
    // 校验订单状态：仅已支付状态的订单可以完成
    if (!PayStatusEnum.PAYMENT.getValue().equals(tradeEntity.getPayStatus())) {
      throw new BusinessException("订单支付状态不处于已支付状态");
    }
    // 修改订单状态为已完成
    TradeEntity updateEntity = new TradeEntity();
    updateEntity.setId(id);
    updateEntity.setOrderStatus(OrderStatusEnum.FINISH.getValue());
    //    FillUserUtil.fillUpdateUserInfo(updateEntity);
    return tradeMapper.update(updateEntity);
  }

  /**
   * 标记订单已退款
   *
   * @param id 订单ID
   * @return 结果
   */
  public int markOrderRefund(@NotNull Long id) {
    TradeEntity tradeEntity = tradeMapper.findById(id);
    AssertUtil.notNull(tradeEntity, "订单不存在");
    // 校验订单状态：仅已支付状态的订单可以退款
    if (!PayStatusEnum.PAYMENT.getValue().equals(tradeEntity.getPayStatus())) {
      throw new BusinessException("订单支付状态不处于已支付状态");
    }
    // 修改订单状态为已退款
    TradeEntity updateEntity = new TradeEntity();
    updateEntity.setId(id);
    // TODO：退款操作与pay模块对接。
    // updateEntity.setOrderStatus(OrderStatusEnum.REJECT.getValue()); // 退款后订单状态为已退货
    updateEntity.setPayStatus(PayStatusEnum.REFUND.getValue()); // 退款后支付状态为已退款
    FillUserUtil.fillUpdateUserInfo(updateEntity);
    return tradeMapper.update(updateEntity);
  }

  /**
   * 订单取消
   *
   * @param id 订单ID
   * @return 结果
   */
  public int cancelOrder(@NotNull Long id) {
    TradeEntity tradeEntity = tradeMapper.findById(id);
    AssertUtil.notNull(tradeEntity, "订单不存在");
    Boolean payCheckres = checkOrderPayFinish(id);
    if (payCheckres) {
      throw new BusinessException("订单已被支付");
    }
    // 校验订单状态：仅待支付状态的订单可以取消
    if (!OrderStatusEnum.CREATE.getValue().equals(tradeEntity.getOrderStatus())) {
      throw new BusinessException("订单不处于待支付状态");
    }
    if (!PayStatusEnum.WAIT_PAY.getValue().equals(tradeEntity.getPayStatus())) {
      throw new BusinessException("订单支付状态不处于待支付状态");
    }
    // 修改订单状态为已取消
    TradeEntity updateEntity = new TradeEntity();
    updateEntity.setId(id);
    updateEntity.setOrderStatus(OrderStatusEnum.CANCEL.getValue());

    int res = tradeMapper.update(updateEntity);
    if (res > 0) {
      // 异步执行
      new Thread(() -> returnStock(id)).start();
    }
    return res;
  }

  /**
   * rabbitmq监听订单取消
   *
   * @param message 消息
   * @note: 该方法是一个消息监听方法，用于监听订单取消消息，如果订单已经支付，则直接返回即可，不需要抛出异常。因此与上一方法cancelOrder有区别。
   */
  @RabbitListener(queues = MqConfig.ORDER_DELAY_Queue_NAME)
  public void cancelOrderFromMQ(Message message) {
    Long id = Long.parseLong(new String(message.getBody()));
    TradeEntity tradeEntity = tradeMapper.findById(id);
    //    log.info("订单超时未支付，检查订单：{}", id);
    if (Objects.isNull(tradeEntity)) {
      return;
    }
    // 先校验是否支付了(有可能支付宝已经支付了，但是消息监听器忽略处理)
    Boolean payCheckres = checkOrderPayFinish(id);
    if (payCheckres) {
      return;
    }
    // 校验订单状态：仅待支付状态的订单可以取消
    if (!OrderStatusEnum.CREATE.getValue().equals(tradeEntity.getOrderStatus())) {
      return;
    }
    if (!PayStatusEnum.WAIT_PAY.getValue().equals(tradeEntity.getPayStatus())) {
      return;
    }
    // 修改订单状态为已取消
    TradeEntity updateEntity = new TradeEntity();
    updateEntity.setId(id);
    updateEntity.setOrderStatus(OrderStatusEnum.CANCEL.getValue());
    int res = tradeMapper.update(updateEntity);
    if (res > 0) {
      returnStock(id);
    }
  }

  /**
   * 归还订单库存
   *
   * @param id 订单ID
   */
  public void returnStock(Long id) {
    // 根据订单id找到所有的订单项
    List<TradeItemEntity> tradeItemEntities = tradeItemService.findByTradeId(id);
    List<CartProductsSimple> cartProductsSimpleList = new ArrayList<>();
    // 遍历订单项，将库存归还
    for (TradeItemEntity tradeItemEntity : tradeItemEntities) {
      CartProductsSimple cartProductsSimple = new CartProductsSimple();
      cartProductsSimple.setProductId(tradeItemEntity.getProductId());
      cartProductsSimple.setQuantity(tradeItemEntity.getQuantity());
      cartProductsSimpleList.add(cartProductsSimple);
    }
    String addStockResult = productFeignClient.addStockInAll(cartProductsSimpleList);
    if (!addStockResult.equals("库存增加成功")) {
      throw new BusinessException("库存归还失败");
    } else {
      return;
    }
  }

  /**
   * 检查订单支付完成
   *
   * @param id 订单ID
   * @return 是否支付完成
   */
  public Boolean checkOrderPayFinish(@NotNull Long id) {
    AssertUtil.notNull(id, "订单ID不能为空");
    TradeEntity tradeEntity = tradeMapper.findById(id);
    AssertUtil.notNull(tradeEntity, "订单不存在");
    // 校验订单状态：仅已支付状态的订单可以完成
    if (PayStatusEnum.PAYMENT.getValue().equals(tradeEntity.getPayStatus())) {
      return true;
    } else {
      // 去redis中查找订单ID
      Boolean res =
          jedisUtil.exists(
              OrderPayFinishByAlipayNotificationService.KEY_NAME + id,
              OrderPayFinishByAlipayNotificationService.PAY_REDIS_DATABASE_INDEX);
      if (res) {
        // redis中存在订单ID，说明订单支付完成.可能是消息监听器忽略处理
        // 提取支付宝流水号
        String tradeNo =
            jedisUtil.get(
                OrderPayFinishByAlipayNotificationService.KEY_NAME + id,
                OrderPayFinishByAlipayNotificationService.PAY_REDIS_DATABASE_INDEX);
        // 修改订单状态为已支付
        markOrderPaid(id, tradeNo, PayTypeEnum.ALIPAY.getValue());
        return true;
      } else {
        return false;
      }
    }
  }
}
