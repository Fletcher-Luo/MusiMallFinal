package com.ByteAndHeartDance.shoppingcart.entity;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用于前端购物车页面展示的实体类
 */
@Data
@Tag(name = "购物车金额信息实体", description = "购物车金额信息实体")
public class ShoppingCartWebEntity {

   /**
    * 总金额
    */
   private BigDecimal totalMoney = BigDecimal.ZERO;

   /**
    * 最终支付金额
    */
   private BigDecimal finalMoney = BigDecimal.ZERO;

   /**
    * 优惠金额
    */
   private BigDecimal subtractMoney = BigDecimal.ZERO;

}
