package com.ByteAndHeartDance.order.entity.otherService;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "购物车商品信息（来自购物车服务）")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartProductsSimple {
    /** 商品ID */
    @Schema(description = "商品ID")
    private Long productId;


    /** 商品数量 */
    @Schema(description = "商品数量")
    private Integer quantity;
}
