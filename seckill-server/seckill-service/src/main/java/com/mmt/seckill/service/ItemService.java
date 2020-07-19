package com.mmt.seckill.service;

import com.mmt.seckill.mapper.ItemMapper;
import com.mmt.seckill.model.Item;
import com.mmt.seckill.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@CacheConfig(cacheNames = "item")
public class ItemService {
    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RedisTemplate redisTemplate;

    public Item getItemById(int itemId) {
        Item item;
        item = (Item) cacheService.getCommonCahe("item_" + itemId);
        if (item == null) {
            while (true) {
                item = (Item) redisTemplate.opsForValue().get("Item_" + itemId);
                if (item != null) {
                    System.out.println("查了缓存");
                    break;
                }
                if (redisTemplate.opsForValue().setIfAbsent("RedisLock_Item_" + itemId, System.currentTimeMillis(), 10L, TimeUnit.SECONDS)) {
                    System.out.println("查了数据库");
                    item = itemMapper.selectById(itemId);
                    redisTemplate.opsForValue().set("Item_" + itemId, item);
                    redisTemplate.delete("RedisLock_Item_" + itemId);
                    break;
                }
            }
            if (item == null) {
                throw new RuntimeException("商品不存在");
            } else {
                cacheService.setCommonCache("item_" + itemId, item);
            }
        }else {
            System.out.println("查了本地缓存");
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return item;
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
