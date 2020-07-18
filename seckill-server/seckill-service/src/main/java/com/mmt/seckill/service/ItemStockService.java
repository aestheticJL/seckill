package com.mmt.seckill.service;

import com.mmt.seckill.mapper.ItemStockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class ItemStockService {
    @Autowired
    private ItemStockMapper itemStockMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    public Integer getItemStockByItemId(int id) {
        return itemStockMapper.getItemStockByItemId(id);
    }

    @Transactional(propagation = Propagation.NESTED)
    public boolean decreaseStockInRedis(int itemId, Integer amount, Integer promoId) {
        Long result = redisTemplate.opsForValue().decrement("promo" + promoId + "_item" + itemId + "_stock", amount.intValue());
        if (result > 0) {
            return true;
        } else if (result == 0) {
            //表示售罄
            redisTemplate.opsForValue().set("promo_item_stock_invalid" + itemId, true, 60 * 60, TimeUnit.SECONDS);
            return true;
        } else {
            throw new RuntimeException("库存不足");
        }
    }


    public boolean decreaseStockInMySQL(int itemId, int amount) {
        return itemStockMapper.decreaseStockInMySQL(itemId, amount);
    }
}
