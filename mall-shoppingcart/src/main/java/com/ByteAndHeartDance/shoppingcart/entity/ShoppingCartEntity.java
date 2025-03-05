package com.ByteAndHeartDance.shoppingcart.entity;

import com.ByteAndHeartDance.shoppingcart.entity.userproduct.UserProductEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.io.Serial;
import java.math.BigDecimal;


@EqualsAndHashCode(callSuper = true)
@Schema(description = "购物车实体")
@Data
@NoArgsConstructor
@AllArgsConstructor
// 类级别的校验，不是直接对字段进行校验，需要结合类中其他校验注解
@Validated
public class ShoppingCartEntity extends UserProductEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "数量不能为空")
    //数量必须大于0
    @Min(value = 1, message = "数量必须大于0")
    @Schema(description = "商品数量")
    private Integer quantity;

    /**
     * 总金额
     */
    @Schema(description = "总金额,不在数据库中存储，服务层计算或者前端计算，需要展示")
    private BigDecimal totalAmount;
}