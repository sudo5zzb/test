package com.bonc.usdp.zzb.test.es;

import com.bonc.usdp.odk.redis.IRedisHandler;
import com.bonc.usdp.odk.redis.standalone.StandaloneHandler;
import com.bonc.usdp.zzb.test.DbUtil.Redis;

import java.time.Instant;
import java.util.stream.IntStream;

public class RedisTest {
    public static void main(String[] args) {
        StandaloneHandler redisHandler = (StandaloneHandler) Redis.getHandler();
        long start= Instant.now().toEpochMilli();
        IntStream.range(0, 10000).forEach(i -> {
            redisHandler.set(i + "", i + "");
        });
        long l=Instant.now().toEpochMilli()-start;
        System.out.println(l);
        IntStream.range(0, 10000).forEach(i -> {
            redisHandler.get(i + "");
        });
        l=Instant.now().toEpochMilli()-start-l;
        System.out.println(l);
        redisHandler.close();
    }
}
