package com.mmt.seckill.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmt.seckill.mapper.*;
import com.mmt.seckill.model.ItemStock;
import com.mmt.seckill.model.OrderInfo;
import com.mmt.seckill.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ItemStockMapper itemStockMapper;
    @Autowired
    private PromoMapper promoMapper;
    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Transactional
    public RespBean createOrder(int userId, Integer itemId, Integer amount, Integer promoId) {
        if (itemMapper.selectById(itemId) == null) {
            throw new RuntimeException("商品不存在");
        }
        if (userMapper.selectById(userId) == null) {
            throw new RuntimeException("用户不存在");
        }
        ItemStock itemStock = itemStockMapper.selectOne(new QueryWrapper<ItemStock>().eq("item_id", itemId));
        if (amount > itemStock.getStock()) {
            throw new RuntimeException("库存不足");
        }
        itemStockMapper.decreaseStock(itemId, amount);
        OrderInfo order = new OrderInfo();
        order.setItemId(itemId);
        order.setUserId(userId);
        order.setAmount(amount);
        order.setPromoId(promoId);
        order.setItemPrice(itemMapper.selectById(itemId).getPrice());
        order.setOrderPrice(promoMapper.selectById(promoId).getPromoItemPrice());
        if (orderInfoMapper.insert(order) == 1) {
            return RespBean.ok("下单成功");
        } else {
            return RespBean.error("下单失败");
        }

    }
}
