package com.ByteAndHeartDance.order.entity.VO;

// import com.ByteAndHeartDance.enums.OrderStatusEnum;
// import com.ByteAndHeartDance.order.enums.PayStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "订单明细实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TradeItemVO {

  /** 订单ID */
  @Schema(name = "tradeId", description = "订单ID")
  private Long tradeId;

  /** 商品ID */
  @Schema(name = "productId", description = "商品ID")
  private Long productId;

  /** 商品名称 */
  @Schema(name = "productName", description = "商品名称")
  private String productName;

  /** 商品规格 */
  @Schema(name = "model", description = "商品规格")
  private String model;

  /** 单价 */
  @Schema(name = "price", description = "商品单价")
  private BigDecimal price;

  /** 数量 */
  @Schema(name = "quantity", description = "购买数量")
  private Integer quantity;

  /** 金额 */
  @Schema(name = "amount", description = "购买商品总金额")
  private BigDecimal amount;

  /** 商品图片 */
  @Schema(name = "picture", description = "商品图片")
  private String picture;
}
