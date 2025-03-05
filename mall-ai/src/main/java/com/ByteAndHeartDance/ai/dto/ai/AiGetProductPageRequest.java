package com.ByteAndHeartDance.ai.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiGetProductPageRequest {
  private String query;
  private int page;
  private String token;
}
