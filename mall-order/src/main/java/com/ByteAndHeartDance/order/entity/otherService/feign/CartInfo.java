package com.ByteAndHeartDance.order.entity.otherService.feign;


import com.ByteAndHeartDance.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "购物车信息")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartInfo  extends BaseEntity {

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称，不在数据库中存储，服务层调用feign传递查询，用于展示")
    private String userName;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称，不在数据库中存储，服务层调用feign传递查询，用于展示")
    private String productName;

    /**
     * 商品规格
     */
    @Schema(description = "规格，不在数据库中存储，服务层调用feign传递查询，用于展示")
    private String model;

    /**
     * 商品价格
     */
    @Schema(description = "商品价格，不在数据库中存储，服务层调用feign传递查询，用于展示")
    private BigDecimal price;

    /**
     * 封面链接
     */
    @Schema(description = "封面链接，不在数据库中存储，服务层调用feign传递查询，用于展示")
    private String cover;

    /**
     * 库存
     */
    @Schema(description = "库存，不在数据库中存储，服务层调用feign传递查询，用于展示")
    private Integer stock;

    /**
     * 商品数量
     */
   @Schema(description = "商品数量")
    private Integer quantity;
}
