package com.ByteAndHeartDance.order.service.trade;

import com.ByteAndHeartDance.order.enums.PayTypeEnum;
import com.ByteAndHeartDance.order.utils.JedisUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** 订单支付宝支付完成的通知服务 */
@Service
@Slf4j
public class OrderPayFinishByAlipayNotificationService {

  @Autowired private JedisUtil jedisUtil;
  @Autowired private TradeService tradeService;

  public static final int PAY_REDIS_DATABASE_INDEX = 1;
  public static final String KEY_NAME = "order:pay:order_";

  @PostConstruct
  public void startListening() {
    // 创建一个单线程的线程池来执行异步任务
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(
        () -> {
          try (Jedis jedis = jedisUtil.getJedis()) {
            jedis.select(PAY_REDIS_DATABASE_INDEX);
            jedis.configSet("notify-keyspace-events", "KEA");

            // 验证配置是否设置成功
            log.info("拿到jedis连接");
            // 创建一个JedisPubSub实例来处理消息
            JedisPubSub pubSub =
                new JedisPubSub() {
                  @Override
                  public void onMessage(String channel, String message) {
                    // 检查是否是键创建事件，并且键名为order
                    if (channel.startsWith(
                            "__keyspace@" + PAY_REDIS_DATABASE_INDEX + "__:" + KEY_NAME)
                        && message.equals("set")) {
                      try {
                        // 从键名中提取订单ID
                        String orderId =
                            channel.substring(channel.lastIndexOf(KEY_NAME) + KEY_NAME.length());
                        // 从值中提取支付宝流水号
                        String tradeNo =
                            jedisUtil.get(KEY_NAME + orderId, PAY_REDIS_DATABASE_INDEX);
                        Long orderIdLong = Long.parseLong(orderId);
                        log.info("订单支付完成，订单ID：{}，支付宝流水号：{}", orderIdLong, tradeNo);
                        // 支付方式为支付宝
                        tradeService.markOrderPaid(
                            orderIdLong, tradeNo, PayTypeEnum.ALIPAY.getValue());
                      } catch (Exception e) {
                        log.error("订单支付完成的通知处理失败", e);
                      }
                    }
                  }

                  @Override
                  public void onPMessage(String pattern, String channel, String message) {
                    onMessage(channel, message);
                  }
                };

            // 订阅键空间通知频道
            jedis.psubscribe(
                pubSub, "__keyspace@" + PAY_REDIS_DATABASE_INDEX + "__:" + KEY_NAME + "*");
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
    log.info("开始监听订单支付完成的通知");
  }
}
