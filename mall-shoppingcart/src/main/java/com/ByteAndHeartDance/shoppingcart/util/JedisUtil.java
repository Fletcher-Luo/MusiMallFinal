package com.ByteAndHeartDance.shoppingcart.util;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/** Jedis工具类：以String类型为例，封装部分方法 */
@Slf4j
@Component
public class JedisUtil {

  @Value("${spring.data.redis.database:4}")
  private int database;

  //private static final int PRODUCT_REDIS_DATABASE = 3; // 商品库存的redis库

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
   * 封装hset方法
   * @param key
   * @param field
   * @param value
   */
  public void hset(String key, String field, String value) {
    init();
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.select(this.database);
      jedis.hset(key, field, value);
    }catch (Exception e) {
      log.error("Jedis hset error", e);
    }
  }

  /**
   * 封装hgetall方法
   * @param key
   * @return
   */
  public Map<String, String> hgetall(String key) {
    init();
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.select(this.database);
      return jedis.hgetAll(key);
    }catch (Exception e) {
      log.error("Jedis hgetall error", e);
    }
    return null;
  }
  public String hget(String key, String field, String value) {
    init();
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.select(this.database);
      return jedis.hget(key, field);
    }catch (Exception e) {
      log.error("Jedis hget error", e);
    }
    return null;
  }
  public void hsetWithExpire(String key, String field, String value, long expireTime) {
    init();
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.select(this.database);
      jedis.hset(key, field, value);
      jedis.expire(key, expireTime);
    }catch (Exception e) {
      log.error("Jedis hsetWithExpire error", e);
    }
  }
  public void setWithExpire(String key, String field, long expireTime) {
    init();
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.select(this.database);
      jedis.setex(key, expireTime, field);
    }catch (Exception e) {
      log.error("Jedis setWithExpire error", e);
    }
  }
//  /**
//   * 执行lua脚本
//   * @param script lua脚本
//   * @return 执行结果
//   */
//  public Object evalInProductDB(String script, List<String> keys, List<String> args) {
//    init();
//    try (Jedis jedis = jedisPool.getResource()) {
//      jedis.select(PRODUCT_REDIS_DATABASE);
//      return jedis.eval(script, keys, args);
//    } catch (Exception e) {
//      log.error("Jedis eval error", e);
//      return null;
//    }
//  }
}

