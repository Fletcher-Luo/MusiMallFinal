package com.mall.product_service.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.List;

/** Jedis工具类：以String类型为例，封装部分方法 */
@Slf4j
@Component
public class JedisUtil {

  @Value("${spring.data.redis.database:3}")
  private int database;

  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.port:6379}")
  private int port;

  @Value("${spring.data.redis.password:}")
  private String password;

  @Value("${spring.data.redis.timeout:5000}")
  private int timeout;

  private JedisPool jedisPool;

  public void init() {
    if (jedisPool == null) {
      jedisPool = new JedisPool(host, port, null, password);
    }
  }

  /**
   * 设置key-value
   *
   * @param key 键
   * @param value 值
   */
  public void set(String key, String value) {
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.select(database);
      jedis.set(key, value);
    } catch (Exception e) {
      log.error("Jedis set error", e);
    }
  }

  /**
   * 删除以key为前缀的所有key
   *
   * @param keyPrefix key前缀
   */
  public void delKeysWithPrefix(String keyPrefix) {
    log.info("删除以{}为前缀的所有key", keyPrefix);
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.select(database);
      String cursor = ScanParams.SCAN_POINTER_START;
      ScanParams scanParams = new ScanParams();
      scanParams.match( keyPrefix+"*");
      scanParams.count(100);

      do {
        ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
        List<String> keys = scanResult.getResult();
        cursor = scanResult.getCursor();
        log.info("删除key: {}", keys);

        if (!keys.isEmpty()) {
          Pipeline pipeline = jedis.pipelined();
          for (String key : keys) {
            pipeline.del(key);

          }
          pipeline.sync();
        }
      } while (!cursor.equals(ScanParams.SCAN_POINTER_START));

    } catch (Exception e) {
      log.error("Jedis set error", e);
    }
  }
}
