package cc.xuhao.herostory.util;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/7/2 14:44
 */
@Slf4j
public final class RedisUtil {

    private static JedisPool jedisPool;

    private RedisUtil() {}

    public static void init() {
        DefaultJedisClientConfig config = DefaultJedisClientConfig
                .builder()
                .password("Xuhao12123..")
                .database(12)
                .build();
        try {
            jedisPool = new JedisPool(new HostAndPort("10.0.0.13", 6379), config);
            log.info("init redis success");
        } catch (Exception e) {
            log.error("init redis error", e);
        }
    }

    public static Jedis getJedis() {
        if (null == jedisPool) {
            throw new IllegalStateException("redis not initialized");
        }

        Jedis jedis = jedisPool.getResource();
        return jedis;
    }

    public static void returnJedis(Jedis jedis) {
        if (null == jedisPool) {
            throw new IllegalStateException("redis not initialized");
        }

        jedisPool.returnResource(jedis);
    }
}
