package com.mmt.seckill.service;

import com.mmt.seckill.mapper.StockLogMapper;
import com.mmt.seckill.model.StockLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class StockLogService {
    @Autowired
    private StockLogMapper stockLogMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    public String createStockLog(Integer itemId, Integer amount) {
        String stockLogId = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set("stockLogId_" + stockLogId, 0, 60, TimeUnit.SECONDS);
        return stockLogId;
    }

    public void changeStatus(String stockLogId, Integer status) {
        redisTemplate.opsForValue().set("stockLogId_" + stockLogId, status, 60 * 60, TimeUnit.SECONDS);
    }
}
