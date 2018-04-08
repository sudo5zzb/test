package com.bonc.usdp.zzb.test.dev.cache;


import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;


public class Cache {


    private static com.google.common.cache.Cache<Object, Object> cache = CacheBuilder.newBuilder()
            .maximumSize(100000)
            .initialCapacity(10)
            .expireAfterAccess(10, TimeUnit.HOURS).build();

    public static void main(String[] args) {
        cache.put("1","111");
        System.out.println(cache.getIfPresent("1"));
    }
}