package com.mmt.seckill.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {
    private Cache<String, Object> commonCache = null;

    @PostConstruct
    public void init() {
        commonCache = CacheBuilder.newBuilder()
                //设置初始容量为10
                .initialCapacity(10)
                //最大缓存100个key，超过100个以后按lru（最近最少使用）策略移除
                .maximumSize(100)
                //写入之后60s后过期
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .build();
    }

    public void setCommonCache(String key, Object value) {
        commonCache.put(key, value);
    }

    public Object getCommonCahe(String key) {
        return commonCache.getIfPresent(key);
    }
}
