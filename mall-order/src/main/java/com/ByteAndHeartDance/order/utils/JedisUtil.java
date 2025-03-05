package com.ByteAndHeartDance.order.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

/** Jedis工具类：以String类型为例，封装部分方法 */
@Slf4j
@Component
public class JedisUtil {

  @Value("${spring.data.redis.database:1}")
  private int database;

  private static final int PRODUCT_REDIS_DATABASE = 3; // 商品库存的redis库

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

  public void set(String key, String value) {
    init();
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.select(this.database);
      jedis.set(key, value);
    } catch (Exception e) {
      log.error("Jedis set error", e);
    }
  }

  /**
   * 设置key-value，并设置过期时间
   *
   * @param key 键
   * @param value 值
   * @param expireTime 过期时间
   */
  public void set(String key, String value, long expireTime) {
    init();
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.select(this.database);
      jedis.setex(key, expireTime, value);
    } catch (Exception e) {
      log.error("Jedis set error", e);
    }
  }

  /**
   * 获取key对应的value
   *
   * @param key 键
   * @return key对应的value
   */
  public String get(String key) {
    init();
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.select(this.database);
      return jedis.get(key);
    } catch (Exception e) {
      log.error("Jedis get error", e);
      return null;
    }
  }

  /**
   * 获取key对应的value
   *
   * @param key 键
   * @param database 数据库
   * @return key对应的value
   */
  public String get(String key, int database) {
    init();
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.select(database);
      return jedis.get(key);
    } catch (Exception e) {
      log.error("Jedis get error", e);
      return null;
    }
  }

  /**
   * 删除key对应的value
   *
   * @param key 键
   */
  public void del(String key) {
    init();
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.select(this.database);
      jedis.del(key);
    } catch (Exception e) {
      log.error("Jedis del error", e);
    }
  }

  /**
   * 删除key对应的value
   *
   * @param key 键
   * @param database 数据库
   */
  public void del(String key, int database) {
    init();
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.select(database);
      jedis.del(key);
    } catch (Exception e) {
      log.error("Jedis del error", e);
    }
  }

  /**
   * 执行lua脚本
   *
   * @param script lua脚本
   * @return 执行结果
   */
  public Object evalInProductDB(String script, List<String> keys, List<String> args) {
    init();
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.select(PRODUCT_REDIS_DATABASE);
      return jedis.eval(script, keys, args);
    } catch (Exception e) {
      log.error("Jedis eval error", e);
      return null;
    }
  }

  /** 获取jedis实例 */
  public Jedis getJedis() {
    init();
    return jedisPool.getResource();
  }

  /**
   * 判断key是否存在
   * @param s key
   * @param DatabaseIndex 数据库索引
   * @return 是否存在
   */
  public Boolean exists(String s, int DatabaseIndex) {
    init();
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.select(DatabaseIndex);
      return jedis.exists(s);
    } catch (Exception e) {
      log.error("Jedis exists error", e);
      return false;
    }
  }
}
