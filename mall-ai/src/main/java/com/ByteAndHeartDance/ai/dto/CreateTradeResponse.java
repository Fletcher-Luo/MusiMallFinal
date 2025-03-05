package com.ByteAndHeartDance.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateTradeResponse {
  private Long traceNo;
  private BigDecimal totalAmount;
  private BigDecimal totalMoneyOriginal;
  private String subject;
}
