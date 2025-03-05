package com.ByteAndHeartDance.shoppingcart.entity.DTO.request;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;


@Data
@Validated
@Tag(name = "修改购物车信息实体", description = "修改购物车信息实体")
public class UpdateShoppingCartDTO {
    @NotNull(message = "商品id不能为空")
    private Long id;
    @Min(value = 1, message = "商品数量不能小于1")
    @NotNull(message = "商品数量不能为空")
    private Integer quantity;
}
