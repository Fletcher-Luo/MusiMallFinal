package com.ByteAndHeartDance.order.entity.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "订单交易创建返回实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateTradeVO {
  /** 系统ID */
  @Schema(description = "系统ID")
  private Long traceNo;

  /** 总需支付金额 */
  @Schema(description = "总需支付金额")
  private BigDecimal totalAmount;

  /** 总金额 */
  @Schema(description = "总金额")
  private BigDecimal totalMoneyOriginal;

  /** 订单标题 */
  @Schema(description = "订单标题")
  private String subject;
}
