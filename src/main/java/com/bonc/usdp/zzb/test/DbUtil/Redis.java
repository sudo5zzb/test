package com.bonc.usdp.zzb.test.DbUtil;

import com.bonc.usdp.odk.redis.IRedisHandler;
import com.bonc.usdp.odk.redis.RedisManager;

public class Redis {
    static RedisManager redisManager = RedisManager.getInstance();
    private static boolean init = false;
    private static String host = "172.16.11.41";
    private static int port = 2468;

    public static IRedisHandler getHandler() {
        if (!init) {
            redisManager.initStandaloneHandlerConfig(host,port);
        }
        return redisManager.getStandaloneHandler();
    }
}
