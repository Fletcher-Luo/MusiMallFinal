package com.ByteAndHeartDance.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

public sealed interface ChatStreamResponse {
  @Data
  @AllArgsConstructor
  final class InitResponse implements ChatStreamResponse {
    private final String chatId;
  }

  @Data
  @AllArgsConstructor
  final class MessageResponse implements ChatStreamResponse {
    private final String message;
  }
}

