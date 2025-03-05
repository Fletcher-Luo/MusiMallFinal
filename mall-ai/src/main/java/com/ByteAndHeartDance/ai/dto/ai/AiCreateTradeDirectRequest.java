package com.ByteAndHeartDance.ai.dto.ai;

import com.ByteAndHeartDance.ai.dto.CreateTradeDirectRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiCreateTradeDirectRequest {
  private String token;
  private CreateTradeDirectRequest createTradeDirectRequest;
}
