package com.ByteAndHeartDance.order.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {
  // 延迟交换机
  public static final String ORDER_DELAY_EXCHANGE_NAME = "order_delay.exchange";
  // 延迟队列
  public static final String ORDER_DELAY_Queue_NAME = "order_delay.queue";
  // 延迟队列路由key
  public static final String ORDER_DELAY_Queue_ROUTING_KEY = "order_delay.queue.routingkey";

  // 添加json格式序列化器
  @Bean
  public MessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  // 声明延迟交换机
  @Bean
  public DirectExchange orderDelayExchange() {
    return ExchangeBuilder.directExchange(ORDER_DELAY_EXCHANGE_NAME)
        // delayed标记当前交换机是一个具备延迟效果的交换机，类型默认是direct直接模式
        .delayed()
        .durable(true)
        .build();
  }

  // 声明延迟队列
  @Bean
  public Queue orderDelayQueue() {
    return QueueBuilder.durable(ORDER_DELAY_Queue_NAME).build();
  }

  // 声明延迟队列的绑定关系
  @Bean
  public Binding delayBinding(
      @Qualifier("orderDelayQueue") Queue queue,
      @Qualifier("orderDelayExchange") DirectExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(ORDER_DELAY_Queue_ROUTING_KEY);
  }
}
