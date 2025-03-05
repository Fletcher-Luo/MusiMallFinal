package com.ByteAndHeartDance.order.entity.otherService;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "商品具体信息（来自商品服务）")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductInfo {
    /** 商品ID */
    @Schema(description = "商品ID")
    private Long id;

    /** 商品名称 */
    @Schema(description = "商品名称")
    private String name;

    /** 商品描述 */
    @Schema(description = "商品描述")
    private String description;

    /** 商品价格 */
    @Schema(description = "商品价格")
    private float price;

    /** 商品图片 */
    @Schema(description = "商品图片")
    private String picture;



}
