package com.mmt.seckill.service;

import com.mmt.seckill.mapper.ItemStockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemStockService {
    @Autowired
    ItemStockMapper itemStockMapper;

    public Boolean decreaseStock(int item_id, Integer amount) {
        return itemStockMapper.decreaseStock(item_id, amount);
    }
}
