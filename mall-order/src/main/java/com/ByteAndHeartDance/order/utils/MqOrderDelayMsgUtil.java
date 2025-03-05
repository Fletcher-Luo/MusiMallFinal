package com.ByteAndHeartDance.order.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ByteAndHeartDance.order.config.MqConfig;

/** 消息队列延迟消息工具类(订单) */
@Component
@Slf4j
public class MqOrderDelayMsgUtil {
  @Autowired private MqUtil mqUtil;

  public static final Long ORDER_DELAY_TIME = 1000 * 60 * 15L; // 订单超时：15分钟（15分钟内必须支付）

  // public static final Long ORDER_DELAY_TIME = 1000 * 10 * 1L; // 10秒（仅供测试）

  // 发送延迟消息（订单超时）
  public void sendOrderDelayMsg(Long tradeId) {
    mqUtil.sendDelayMsg(
        MqConfig.ORDER_DELAY_EXCHANGE_NAME,
        MqConfig.ORDER_DELAY_Queue_ROUTING_KEY,
        tradeId,
        ORDER_DELAY_TIME);
  }
}
