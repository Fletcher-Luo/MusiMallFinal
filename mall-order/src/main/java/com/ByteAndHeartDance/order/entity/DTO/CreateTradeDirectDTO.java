package com.ByteAndHeartDance.order.entity.DTO;

import com.ByteAndHeartDance.order.entity.otherService.CartProductsSimple;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "AI订单交易创建实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateTradeDirectDTO {
  /** 系统ID */
  @Schema(description = "系统ID")
  private Long id;

  /** 送货地址ID */
  @Schema(description = "送货地址ID")
  private Long deliveryAddressId;

  /** 购物信息 */
  @Schema(description = "购物信息")
  private List<CartProductsSimple> cartProducts;

  /** 备注 */
  @Schema(description = "备注")
  private String remark;
}
