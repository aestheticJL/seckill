package com.mmt.seckill.controller;

import com.mmt.seckill.model.Item;
import com.mmt.seckill.service.CacheService;
import com.mmt.seckill.service.ItemService;
import com.mmt.seckill.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired
    private ItemService itemService;

    @Autowired
    private CacheService cacheService;

    @GetMapping("/{id}")
    public Item getItemById(@PathVariable("id") int id) {
        Item item;
        item = (Item) cacheService.getCommonCahe("item_" + id);
        if (item == null) {
            item = itemService.getItemById(id);
            cacheService.setCommonCache("item_" + id, item);
        }
        return item;
    }

    @GetMapping("/")
    public List<Item> getAllItems() {
        List<Item> allItems;
        allItems = (List<Item>) cacheService.getCommonCahe("items");
        if (allItems == null) {
            allItems = itemService.getAllItems();
            cacheService.setCommonCache("items", allItems);
        }
        return allItems;
    }

    @PostMapping("/")
    public RespBean createItem(@RequestBody Item item) {
        if (cacheService.getCommonCahe("items") != null) {
            cacheService.setCommonCache("items", null);
        }
        return itemService.createItem(item);
    }
}
