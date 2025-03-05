package com.ByteAndHeartDance.shoppingcart.entity.DTO.request;



import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Tag(name = "添加购物车信息实体", description = "添加购物车信息实体" )
public class AddShoppingCartDTO {
  @NotNull(message = "商品id不能为空")
  private Long productId;
  @NotNull(message = "商品数量不能为空")
  @Min(value = 1, message = "商品数量不能小于1")
  private Integer quantity;
}
