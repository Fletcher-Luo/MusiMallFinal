package com.ByteAndHeartDance.ai.dto;

import com.ByteAndHeartDance.ai.entity.CartProducts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateTradeDirectRequest {
  private Long id;
  private Long deliveryAddressId;
  private List<CartProducts> cartProducts;
  private String remark;
  private String token;
}