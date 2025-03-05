package com.mall.product_service.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 商品实体类（Product）
 * 该类直接映射到数据库中的 products 表。
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "商品扣减信息实体")
public class ProductsDecreaseInfoDTO {

    @Schema(description = "系统ID")
    private Long productId; // 数据库中产品ID，类型为 Long

    @Schema(description = "购买商品数量")
    private int quantity;// 注意这里是购买商品的数量


}
