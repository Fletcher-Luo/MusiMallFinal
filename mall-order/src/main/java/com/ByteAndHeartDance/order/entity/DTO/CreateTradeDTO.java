package com.ByteAndHeartDance.order.entity.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "订单交易创建实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateTradeDTO {
  /** 系统ID */
  @Schema(description = "系统ID")
  private Long id;

  /** 送货地址ID */
  @Schema(description = "送货地址ID")
  private Long deliveryAddressId;



  /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
