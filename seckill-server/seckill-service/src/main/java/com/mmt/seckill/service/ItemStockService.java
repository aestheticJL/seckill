package com.mmt.seckill.service;

import com.mmt.seckill.mapper.ItemStockMapper;
import com.mmt.seckill.service.rocketMQ.MqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemStockService {
    @Autowired
    private ItemStockMapper itemStockMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    public Integer getItemStockByItemId(int id) {
        return itemStockMapper.getItemStockByItemId(id);
    }

    @Transactional
    public boolean decreaseStockInRedis(int itemId, Integer amount) {
        Long result = redisTemplate.opsForValue().decrement("stock" + itemId, amount.intValue());
        if (result > 0) {
            return true;
        } else if (result == 0) {
            //表示售罄
            redisTemplate.opsForValue().set("promo_item_stock_invalid" + itemId, true);
            return true;
        } else {
            redisTemplate.opsForValue().increment("stock" + itemId, amount.intValue());
            return false;
        }
    }


    public boolean decreaseStockInMySQL(int itemId, int amount) {
        return itemStockMapper.decreaseStockInMySQL(itemId, amount);
    }
}
