package com.ByteAndHeartDance.ai.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiGetUserNameResponse {
  private String userName;
  private String info;
}
