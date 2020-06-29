package com.mmt.seckill.service;

import com.mmt.seckill.mapper.ItemMapper;
import com.mmt.seckill.model.Item;
import com.mmt.seckill.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "item")
public class ItemService {
    @Autowired
    private ItemMapper itemMapper;

    @Cacheable(key = "#id", unless = "#result==null")
    public Item getItemById(int id) {
        return itemMapper.selectById(id);
    }

    @Cacheable(key = "'allItems'", unless = "#result==null")
    public List<Item> getAllItems() {
        return itemMapper.selectList(null);
    }

    @CacheEvict(key = "'allItems'")
    public RespBean createItem(Item item) {
        if (itemMapper.insert(item) != 0) {
            return RespBean.ok("添加商品成功");
        } else {
            return RespBean.error("添加商品失败");
        }
    }

}
