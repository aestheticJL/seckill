package com.mmt.seckill.service;

import com.mmt.seckill.mapper.PromoMapper;
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

    @Cacheable(cacheNames = "promo", key = "#promoId", unless = "#result==null")
    public Promo getPromoById(Integer promoId) {
        return promoMapper.selectById(promoId);
    }

    public RespBean createPromo(Promo promo) {
        if (promoMapper.insert(promo) == 1) {
            getPromoById(promo.getId());
            //设置库存到redis中去
            Integer itemStock = itemStockService.getItemStockByItemId(promo.getItemId());
            redisTemplate.opsForValue().set("promo" + promo.getId() + "_item" + promo.getItemId() + "_stock", itemStock, 60 * 60, TimeUnit.SECONDS);
            //设置大闸
            redisTemplate.opsForValue().set("promo" + promo.getId() + "_door_count", itemStock * 5);
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
//        Promo promo = getPromoById(promoId);
//        if (!(promo.getStartDate().after(new Timestamp(new Date().getTime())) && promo.getEndDate().before(new Timestamp(new Date().getTime())))) {
//            return null;
//        }
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
