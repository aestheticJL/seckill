package com.mmt.seckill.service;

import com.mmt.seckill.mapper.ItemStockMapper;
import com.mmt.seckill.service.rocketMQ.MqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ItemStockService {
    @Autowired
    private ItemStockMapper itemStockMapper;
    @Autowired
    private MqProducer mqProducer;
    @Autowired
    private RedisTemplate redisTemplate;

    public int getItemStockByItemId(int id) {
        return itemStockMapper.getItemStockByItemId(id);
    }

    public Boolean decreaseStockInRedis(int item_id, Integer amount) {
        Long result = redisTemplate.opsForValue().decrement("stock" + item_id, amount.intValue());
        if (result >= 0) {
            Boolean mqResult = mqProducer.asyncReduceStock(item_id, amount);
            if (!mqResult) {
                redisTemplate.opsForValue().increment("stock" + item_id, amount.intValue());
                return false;
            }
            return true;
        } else {
            redisTemplate.opsForValue().increment("stock" + item_id, amount.intValue());
            return false;
        }
    }
}
