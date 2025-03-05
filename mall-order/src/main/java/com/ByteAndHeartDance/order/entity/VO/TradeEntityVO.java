package com.ByteAndHeartDance.order.entity.VO;

// import com.ByteAndHeartDance.enums.OrderStatusEnum;
// import com.ByteAndHeartDance.order.enums.PayStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "订单交易返回实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TradeEntityVO {
  /** 系统ID */
  @Schema(name = "id", description = "系统ID")
  private Long id;

  /** 订单编码 */
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

  /** 支付方式 */
  @Schema(name = "payType", description = "支付方式")
  private Integer payType;

  /** 支付流水号 */
  @Schema(name = "alipayTraceNo", description = "支付流水号")
  private String alipayTraceNo;

  /** 送货地址ID */
  @Schema(description = "送货地址ID")
  private Long deliveryAddressId;
}
