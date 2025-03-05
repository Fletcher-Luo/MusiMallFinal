package com.ByteAndHeartDance.ai.dto.ai;

import com.ByteAndHeartDance.ai.dto.ProductDTO;
import com.ByteAndHeartDance.ai.dto.ResponsePage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiGetProductPageResponse {
  private ResponsePage<ProductDTO> responsePage;
  private String info;
}
