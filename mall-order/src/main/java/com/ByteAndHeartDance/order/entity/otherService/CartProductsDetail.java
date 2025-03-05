package com.ByteAndHeartDance.order.entity.otherService;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "购物车商品详细信息（来自购物车服务）")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartProductsDetail {
    /** 商品ID */
    @Schema(description = "商品ID")
    private Long productId;


    /** 商品数量 */
    @Schema(description = "商品数量")
    private Integer quantity;

    /** 商品名称 */
    @Schema(description = "商品名称")
    private String productName;

    /** 商品规格 */
    @Schema(description = "商品规格")
    private String model;

    /** 商品单价 */
    @Schema(description = "商品单价")
    private BigDecimal price;

    /** 商品封面 */
    @Schema(description = "商品封面")
    private String cover;

    /** 商品库存 */
    @Schema(description = "商品库存")
    private Integer stock;

    /** 商品总金额 */
    @Schema(description = "商品总金额")
    private BigDecimal totalAmount;

}




