package com.mmt.seckill.service;

import com.mmt.seckill.mapper.OrderInfoMapper;
import com.mmt.seckill.model.Item;
import com.mmt.seckill.model.OrderInfo;
import com.mmt.seckill.model.Promo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    @Autowired
    private ItemStockService itemStockService;
    @Autowired
    private PromoService promoService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private StockLogService stockLogService;

    @Transactional
    public boolean createOrder(int userId, Integer itemId, Integer amount, Integer promoId, String stockLogId) {
        Item item;
        item = (Item) cacheService.getCommonCahe("item_" + itemId);
        if (item == null) {
            item = itemService.getItemById(itemId);
            if (item == null) {
                throw new RuntimeException("商品不存在");
            } else {
                cacheService.setCommonCache("item_" + itemId, item);
            }
        }
        Promo promo = promoService.getPromoById(promoId);
        if (promo == null) {
            throw new RuntimeException("活动未开始");
        }
        if (!itemStockService.decreaseStockInRedis(itemId, amount)) {
            throw new RuntimeException("库存不足");
        }
        OrderInfo order = new OrderInfo();
        order.setItemId(itemId);
        order.setUserId(userId);
        order.setAmount(amount);
        order.setPromoId(promoId);
        order.setItemPrice(item.getPrice());
        order.setOrderPrice(promo.getPromoItemPrice());

        if (orderInfoMapper.insert(order) == 1) {
            stockLogService.changeStatus(stockLogId, 1);
            return true;
        } else {
            throw new RuntimeException("下单失败");
        }
    }
}
