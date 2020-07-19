package com.mmt.seckill.service;

import com.alibaba.fastjson.JSON;
import com.mmt.seckill.mapper.PromoMapper;
import com.mmt.seckill.model.Item;
import com.mmt.seckill.model.ItemStock;
import com.mmt.seckill.model.Promo;
import com.mmt.seckill.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PromoService {
    @Autowired
    private PromoMapper promoMapper;
    @Autowired
    private ItemStockService itemStockService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private CacheService cacheService;

    public Promo getPromoById(Integer promoId) {
        Promo promo;
        promo = (Promo) cacheService.getCommonCahe("Promo_" + promoId);
        if (promo == null) {
            while (true) {
                promo = (Promo) redisTemplate.opsForValue().get("Promo_" + promoId);
                if (promo != null) {
                    System.out.println("查了缓存");
                    break;
                }
                if (redisTemplate.opsForValue().setIfAbsent("RedisLock_Promo_" + promoId, System.currentTimeMillis(), 10L, TimeUnit.SECONDS)) {
                    System.out.println("查了数据库");
                    promo = promoMapper.selectById(promoId);
                    redisTemplate.opsForValue().set("Promo_" + promoId, promo);
                    redisTemplate.delete("RedisLock_Promo_" + promoId);
                    break;
                }
                Thread.yield();
            }
            if (promo == null) {
                throw new RuntimeException("活动不存在");
            } else {
                cacheService.setCommonCache("Promo_" + promoId, promo);
            }
        }else {
            System.out.println("查了本地缓存");
        }
        return promo;
    }

    public RespBean createPromo(Promo promo) {
        if (promoMapper.insert(promo) == 1) {
            getPromoById(promo.getId());
            //设置库存到redis中去
            Integer itemStock = itemStockService.getItemStockByItemId(promo.getItemId());
            redisTemplate.opsForValue().set("promo" + promo.getId() + "_item" + promo.getItemId() + "_stock", itemStock, 60 * 60, TimeUnit.SECONDS);
            //设置大闸
            redisTemplate.opsForValue().set("promo" + promo.getId() + "_door_count", itemStock * 5, 60 * 60, TimeUnit.SECONDS);
            return RespBean.ok("活动创建成功");
        } else {
            return RespBean.error("活动创建失败");
        }
    }

    public String createPromoToken(Integer promoId, Integer itemId, Integer userId) {
        //判断库存是否售罄
        if (redisTemplate.hasKey("promo_item_stock_invalid" + itemId)) {
            return null;
        }
        if (userService.selectById(userId) == null) {
            return null;
        }
        //减大闸
        Long result = redisTemplate.opsForValue().decrement("promo" + promoId + "_door_count");
        if (result < 0) {
            return null;
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set("promo" + promoId + "_item" + itemId + "_user" + userId + "_token", token, 60 * 60, TimeUnit.SECONDS);
        return token;
    }
}
