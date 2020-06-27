package com.mmt.seckill.service;

import com.mmt.seckill.mapper.ItemMapper;
import com.mmt.seckill.model.Item;
import com.mmt.seckill.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
    @Autowired
    private ItemMapper itemMapper;

    public Item getItemById(int id) {
        return itemMapper.selectById(id);
    }


    public RespBean createItem(Item item) {
        if (itemMapper.insert(item) != 0) {
            return RespBean.ok("添加商品成功");
        } else {
            return RespBean.error("添加商品失败");
        }
    }

    public List<Item> getAllItems() {
        return itemMapper.selectList(null);
    }
}
