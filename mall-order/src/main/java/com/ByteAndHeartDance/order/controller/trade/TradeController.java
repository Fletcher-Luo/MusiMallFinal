package com.ByteAndHeartDance.order.controller.trade;

import com.ByteAndHeartDance.entity.ResponsePageEntity;
import com.ByteAndHeartDance.order.entity.DTO.CreateTradeDirectDTO;
import com.ByteAndHeartDance.order.entity.DTO.CreateTradeDTO;
import com.ByteAndHeartDance.order.entity.VO.CreateTradeVO;
import com.ByteAndHeartDance.order.entity.VO.TradeEntityAndItemVO;
import com.ByteAndHeartDance.order.entity.order.trade.TradeConditionEntity;
import com.ByteAndHeartDance.order.entity.order.trade.TradeEntity;
import com.ByteAndHeartDance.order.service.trade.TradeCreateService;
import com.ByteAndHeartDance.utils.AssertUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ByteAndHeartDance.order.service.trade.TradeService;

import java.util.List;

@Tag(name = "订单操作", description = "订单操作接口")
@RestController
@RequestMapping("/v1/trade")
@Validated
@Slf4j
public class TradeController {
  private final TradeService tradeService;
  private final TradeCreateService tradeCreateService;

  public TradeController(TradeService tradeService, TradeCreateService tradeCreateService) {
    this.tradeService = tradeService;
    this.tradeCreateService = tradeCreateService;
  }

  /**
   * 通过id查询订单信息
   *
   * @param id 系统ID
   * @return 订单信息
   */
  @Operation(summary = "通过id查询订单信息", description = "通过id查询订单信息")
  @GetMapping("/findById")
  public TradeEntityAndItemVO findById(Long id) {
    return tradeService.findById(id);
  }

  /**
   * 根据条件分页查询订单列表
   *
   * @param tradeConditionEntity 条件
   * @return 订单列表
   */
  @Operation(summary = "根据条件分页查询订单列表", description = "根据条件查询订单列表")
  @PostMapping("/searchByPage")
  public ResponsePageEntity<TradeEntityAndItemVO> searchByPage(
      @RequestBody TradeConditionEntity tradeConditionEntity) {
    return tradeService.searchByPage(tradeConditionEntity);
  }

  /**
   * 根据条件分页查询当前用户的订单列表
   *
   * @param tradeConditionEntity 条件
   * @return 订单列表
   */
  @Operation(summary = "根据条件分页查询当前用户的订单列表", description = "根据条件查询当前用户的订单列表")
  @PostMapping("/searchByPageCurrentUser")
  public ResponsePageEntity<TradeEntityAndItemVO> searchByPageCurrentUser(
      @RequestBody TradeConditionEntity tradeConditionEntity) {
    return tradeService.searchByPageCurrentUser(tradeConditionEntity);
  }

  /**
   * 预分配订单ID
   *
   * @return 订单ID
   */
  @Operation(summary = "预分配订单ID", description = "预分配订单ID")
  @GetMapping("/preCreateTradeId")
  public Long preCreateTradeId() {
    return tradeService.preCreateTradeId();
  }

  /**
   * 用户下单
   *
   * @param createTradeDTO 创建订单实体
   * @return 订单编号（code/id）
   */
  @Operation(summary = "用户从购物车下单", description = "用户下单")
  @PostMapping("/create")
  public CreateTradeVO create(@RequestBody CreateTradeDTO createTradeDTO) {
    TradeEntity tradeEntity = new TradeEntity();
    tradeEntity.setDeliveryAddressId(createTradeDTO.getDeliveryAddressId());
    tradeEntity.setId(createTradeDTO.getId());
    tradeEntity.setRemark(createTradeDTO.getRemark());
    return tradeCreateService.createOrderTradeFromCart(tradeEntity);
  }

  /**
   * 直接下单
   *
   * @param createTradeDirectDTO 创建订单实体
   * @return 订单编号（code/id）
   */
  @Operation(summary = "直接下单", description = "直接下单")
  @PostMapping("/createByDirect")
  public CreateTradeVO createByAI(@RequestBody CreateTradeDirectDTO createTradeDirectDTO) {
    AssertUtil.notEmpty(createTradeDirectDTO.getCartProducts(), "购物信息不能为空");
    TradeEntity tradeEntity = new TradeEntity();
    tradeEntity.setDeliveryAddressId(createTradeDirectDTO.getDeliveryAddressId());
    tradeEntity.setId(createTradeDirectDTO.getId());
    tradeEntity.setRemark(createTradeDirectDTO.getRemark());
    return tradeCreateService.createOrderTradeDirect(
        tradeEntity, createTradeDirectDTO.getCartProducts());
  }

  /**
   * 修改订单
   *
   * @param tradeEntity 订单实体
   * @return 影响行数
   */
  @Operation(summary = "修改订单", description = "修改订单")
  @PostMapping("/update")
  public int update(@RequestBody TradeEntity tradeEntity) {
    return tradeService.update(tradeEntity);
  }

  /**
   * 批量删除订单
   *
   * @param ids 订单ID集合
   * @return 影响行数
   */
  @Operation(summary = "批量删除订单", description = "批量删除订单")
  @PostMapping("/deleteByIds")
  public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
    return tradeService.deleteByIds(ids);
  }

  /**
   * 更新订单状态为已支付
   *
   * @param id 订单ID
   * @param alipayTraceNo 支付宝交易号
   * @return 影响行数
   */
  @Operation(summary = "更新订单状态为已支付", description = "更新订单状态为已支付")
  @PostMapping("/markOrderPaid")
  public int markOrderPaid(@NotNull Long id, String alipayTraceNo, Integer payType) {
    return tradeService.markOrderPaid(id, alipayTraceNo, payType);
  }

  /**
   * 更新订单状态为已完成
   *
   * @param id 订单ID
   * @return 影响行数
   */
  @Operation(summary = "更新订单状态为已完成", description = "更新订单状态为已完成")
  @PostMapping("/markOrderFinished")
  public int markOrderFinished(@NotNull Long id) {
    return tradeService.markOrderFinished(id);
  }

  /**
   * 取消订单：订单仅在未支付状态下可以取消
   *
   * @param id 订单ID
   * @return 影响行数
   */
  @Operation(summary = "取消订单", description = "订单仅在未支付状态下可以取消")
  @PostMapping("/cancelOrder")
  public int cancelOrder(@NotNull Long id) {
    return tradeService.cancelOrder(id);
  }

  /**
   * 标记订单为已退款：仅在已支付状态下可以退款
   *
   * @param id 订单ID
   * @return 影响行数
   */
  @Operation(summary = "标记订单为已退款", description = "仅在已支付状态下可以退款")
  @PostMapping("/markOrderRefund")
  public int markOrderRefund(@NotNull Long id) {
    return tradeService.markOrderRefund(id);
  }

  /**
   * 检查订单是否使用支付宝支付完成
   *
   * @param id 订单ID
   * @return 是否使用支付宝支付完成
   */
  @Operation(summary = "检查订单是否使用支付宝支付完成", description = "检查订单是否支付完成")
  @GetMapping("/checkOrderPayFinish")
  public Boolean checkOrderPayFinish(@NotNull Long id) {
    return tradeService.checkOrderPayFinish(id);
  }
}
