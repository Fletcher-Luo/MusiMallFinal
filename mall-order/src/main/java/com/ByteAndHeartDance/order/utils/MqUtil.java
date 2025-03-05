package com.ByteAndHeartDance.order.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** 消息队列工具类 @Author: jjq */
@Slf4j
@Component
public class MqUtil {
  @Autowired private RabbitTemplate rabbitTemplate;

  /**
   * 发送消息
   *
   * @param routingKey 路由键
   * @param message 消息
   */
  public void sendMsg(String routingKey, Object message) {
    //    log.info("MQ发送消息： routingKey={}, message={}", routingKey, message);
    rabbitTemplate.convertAndSend(routingKey, message);
  }

  /**
   * 发送消息
   *
   * @param exchange 交换机
   * @param routingKey 路由键
   * @param message 消息
   */
  public void sendMsg(String exchange, String routingKey, Object message) {
    //    log.info("MQ发送消息：exchange={}, routingKey={}, message={}", exchange, routingKey, message);
    rabbitTemplate.convertAndSend(exchange, routingKey, message);
  }

  /**
   * 发送延迟消息(请使用MqDelayMsgUtil类发送消息)
   *
   * @param exchange 交换机
   * @param routingKey 路由键
   * @param message 消息
   * @param delayTime 延迟时间(毫秒)
   */
  public void sendDelayMsg(String exchange, String routingKey, Object message, long delayTime) {
    try {
      rabbitTemplate.convertAndSend(
          exchange,
          routingKey,
          message,
          msg -> {
            // 发送消息的时候延迟时长
            msg.getMessageProperties().setExpiration(String.valueOf(delayTime));
            msg.getMessageProperties().setHeader("x-delay", delayTime);
            return msg;
          });

    } catch (Exception e) {
      log.error("发送延迟消息失败", e);
    }
  }
}
