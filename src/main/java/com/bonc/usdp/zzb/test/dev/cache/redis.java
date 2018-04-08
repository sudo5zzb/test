package com.bonc.usdp.zzb.test.dev.cache;

import com.bonc.usdp.odk.redis.RedisManager;
import com.bonc.usdp.odk.redis.standalone.StandaloneHandler;

public class redis {
    public static void main(String[] args) {
        RedisManager.getInstance().initStandaloneHandlerConfig("127.0.0.1",6379);
        StandaloneHandler standaloneHandler = RedisManager.getInstance().getStandaloneHandler();
        standaloneHandler.set("123","");
        System.out.println(standaloneHandler.get("123"));
        standaloneHandler.close();
        RedisManager.getInstance().closePool();
    }
}
