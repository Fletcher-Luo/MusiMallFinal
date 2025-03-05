package com.ByteAndHeartDance.ai.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  SYSTEM_ERROR(500, "系统错误"),
  PARAMS_ERROR(400, "请求参数错误"),
  NOT_LOGIN(401, "未登录"),
  NO_AUTH(403, "无权限"),
  NOT_FOUND(404, "请求未找到"),
  NOT_FOUND_ERROR(40400, "请求数据不存在"),
  METHOD_NOT_ALLOWED(405, "请求方法不允许"),
  LOCKED(423, "请求失败，请稍后重试"),
  TOO_MANY_REQUESTS(429, "请求过多"),
  ;
  /**
   * 状态码
   */
  private final int code;
  /**
   * 信息
   */
  private final String message;
  ErrorCode(int code, String message) {
    this.code = code;
    this.message = message;
  }
}

