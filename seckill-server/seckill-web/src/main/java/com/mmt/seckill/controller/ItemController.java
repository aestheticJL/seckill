package com.mmt.seckill.controller;

import com.mmt.seckill.utils.RespBean;
import com.mmt.seckill.model.Item;
import com.mmt.seckill.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired
    private ItemService itemService;

    @GetMapping("/{id}")
    public Item getItemById(@PathVariable("id") int id) {
        return itemService.getItemById(id);
    }

    @GetMapping("/")
    public List<Item> getAllItems(){
        return itemService.getAllItems();
    }

    @PostMapping("/")
    public RespBean createItem(@RequestBody Item item) {
        return itemService.createItem(item);
    }
}
