package com.mmt.seckill.service;

import com.mmt.seckill.mapper.PromoMapper;
import com.mmt.seckill.model.ItemStock;
import com.mmt.seckill.model.Promo;
import com.mmt.seckill.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class PromoService {
    @Autowired
    private PromoMapper promoMapper;
    @Autowired
    private ItemStockService itemStockService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Cacheable(cacheNames = "promo", key = "#promoId", unless = "#result==null")
    public Promo getPromoById(Integer promoId) {
        return promoMapper.selectById(promoId);
    }

    public RespBean createPromo(Promo promo) {
        if (promoMapper.insert(promo) == 1) {
            getPromoById(promo.getId());
            redisTemplate.opsForValue().set("stock" + promo.getItemId(), itemStockService.getItemStockByItemId(promo.getItemId()), 60 * 60, TimeUnit.SECONDS);
            return RespBean.ok("活动创建成功");
        } else {
            return RespBean.error("活动创建失败");
        }
    }
}
