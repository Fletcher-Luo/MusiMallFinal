package com.ByteAndHeartDance.order.entity.order.tradeItem;

import com.ByteAndHeartDance.entity.BaseEntity;
// import com.ByteAndHeartDance.enums.OrderStatusEnum;
// import com.ByteAndHeartDance.order.enums.PayStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "订单明细实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TradeItemEntity extends BaseEntity {

  /** 订单ID */
  @NotEmpty(message = "订单ID不能为空")
  @Schema(name = "tradeId", description = "订单ID")
  private Long tradeId;

  /** 商品ID */
  @NotEmpty(message = "商品ID不能为空")
  @Schema(name = "productId", description = "商品ID")
  private Long productId;

  /** 商品名称 */
  @NotEmpty(message = "商品名称不能为空")
  @Schema(name = "productName", description = "商品名称")
  private String productName;

  /** 商品规格 */
  @Schema(name = "model", description = "商品规格")
  private String model;

  /** 单价 */
  @NotNull(message = "单价不能为空")
  @Schema(name = "price", description = "商品单价")
  private BigDecimal price;

  /** 数量 */
  @NotNull(message = "数量不能为空")
  @Schema(name = "quantity", description = "购买数量")
  private Integer quantity;

  /** 金额 */
  @NotNull(message = "金额不能为空")
  @Schema(name = "amount", description = "购买商品总金额")
  private BigDecimal amount;

  /** 商品图片 */
  @Schema(name = "picture", description = "商品图片")
  private String picture;
}
