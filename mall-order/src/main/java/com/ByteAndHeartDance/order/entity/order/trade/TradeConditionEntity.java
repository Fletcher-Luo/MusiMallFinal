package com.ByteAndHeartDance.order.entity.order.trade;

import com.ByteAndHeartDance.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/** 交易查询条件实体 */
@Schema(description = "订单交易查询条件实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TradeConditionEntity extends RequestConditionEntity {
  /** ID */
  @Schema(description = "ID")
  private Long id;

  /** 订单编码 */
  @Schema(description = "订单编码")
  private String code;

  /** 用户ID */
  @Schema(description = "用户ID")
  private Long userId;

  /** 用户名称 */
  @Schema(description = "用户名称")
  private String userName;

  /** 下单时间 */
  @Schema(description = "下单时间")
  private Date orderTime;

  /** 订单状态 */
  @Schema(description = "订单状态")
  //  private OrderStatusEnum orderStatus;
  private Integer orderStatus;

  /** 支付状态 */
  @Schema(description = "支付状态")
  //    private PayStatusEnum payStatus;
  private Integer payStatus;

  /** 总金额 */
  @Schema(description = "总金额", minimum = "0")
  private BigDecimal totalAmount;

  /** 付款金额 */
  @Schema(description = "付款金额", minimum = "0")
  private BigDecimal paymentAmount;

  /** 备注 */
  @Schema(description = "备注")
  private String remark;

  /** 送货地址ID */
  @Schema(description = "送货地址ID")
  private Long deliveryAddressId;

  /** 支付宝支付流水号 */
  @Schema(name = "alipayTraceNo", description = "支付宝支付流水号")
  private String alipayTraceNo;

  /** 支付方式 */
  @Schema(name = "payType", description = "支付方式")
  private Integer payType;

  /** 创建人ID */
  @Schema(description = "创建人ID")
  private Long createUserId;

  /** 创建人名称 */
  @Schema(description = "创建人名称")
  private String createUserName;

  /** 创建日期 */
  @Schema(description = "创建时间")
  private Date createTime;

  /** 修改人ID */
  @Schema(description = "修改人ID")
  private Long updateUserId;

  /** 修改人名称 */
  @Schema(description = "修改人名称")
  private String updateUserName;

  /** 修改时间 */
  @Schema(description = "修改时间")
  private Date updateTime;

  /** 是否删除 1：已删除 0：未删除 */
  @Schema(description = "是否删除 1：已删除 0：未删除")
  private Integer isDel;
}
