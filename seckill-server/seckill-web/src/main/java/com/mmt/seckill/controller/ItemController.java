package com.mmt.seckill.controller;

import com.mmt.seckill.model.Item;
import com.mmt.seckill.service.ItemService;
import com.mmt.seckill.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@CacheConfig(cacheNames = "items")
public class ItemController {
    @Autowired
    private ItemService itemService;

    @GetMapping("/{id}")
    @Cacheable(key = "#id", unless = "#result==null")
    public Item getItemById(@PathVariable("id") int id) {
        return itemService.getItemById(id);
    }

    @GetMapping("/")
    @Cacheable(key = "'allItems'", unless = "#result==null")
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    @PostMapping("/")
    @CacheEvict(key = "'allItems'")
    public RespBean createItem(@RequestBody Item item) {
        return itemService.createItem(item);
    }
}
