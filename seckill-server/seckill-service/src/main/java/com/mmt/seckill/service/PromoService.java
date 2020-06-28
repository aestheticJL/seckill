package com.mmt.seckill.service;

import com.mmt.seckill.mapper.PromoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PromoService {
    @Autowired
    private PromoMapper promoMapper;

    @Cacheable(cacheNames = "promoItemPrice", key = "#promoId", unless = "#result==null")
    public double selectPromoItemPriceById(Integer promoId) {
        return promoMapper.selectPromoItemPriceById(promoId);
    }
}
