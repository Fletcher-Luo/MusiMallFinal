package com.ByteAndHeartDance.ai.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartProducts {
  private Long productId;
  private Integer quantity;
}
