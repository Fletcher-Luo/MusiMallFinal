package com.ByteAndHeartDance.order.entity.order.trade;

import com.ByteAndHeartDance.entity.BaseEntity;
// import com.ByteAndHeartDance.enums.OrderStatusEnum;
// import com.ByteAndHeartDance.order.enums.PayStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "订单交易实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TradeEntity extends BaseEntity {

  /** 订单编码 */
  @NotEmpty(message = "订单编码不能为空")
  @Schema(name = "code", description = "订单编码")
  private String code;

  /** 用户ID */
  @Schema(name = "userId", description = "用户ID")
  private Long userId;

  /** 用户名称 */
  @Schema(name = "userName", description = "用户名称")
  private String userName;

  /** 下单时间 */
  @Schema(name = "orderTime", description = "下单时间")
  private Date orderTime;

  /** 订单状态 */
  @Schema(name = "orderStatus", description = "订单状态")
  //    private OrderStatusEnum orderStatus;
  private Integer orderStatus;

  /** 支付状态 */
  @Schema(name = "payStatus", description = "支付状态")
  //    private PayStatusEnum payStatus;
  private Integer payStatus;

  /** 总金额 */
  @Schema(name = "totalAmount", description = "总金额", minimum = "0")
  private BigDecimal totalAmount;

  /** 付款金额 */
  @Schema(name = "paymentAmount", description = "付款金额", minimum = "0")
  private BigDecimal paymentAmount;

  /** 备注 */
  @Schema(name = "remark", description = "备注")
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
}
