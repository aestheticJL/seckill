package com.mmt.seckill.service;

import com.mmt.seckill.mapper.OrderInfoMapper;
import com.mmt.seckill.model.Item;
import com.mmt.seckill.model.OrderInfo;
import com.mmt.seckill.model.Promo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    @Autowired
    private ItemStockService itemStockService;
    @Autowired
    private PromoService promoService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private StockLogService stockLogService;

    @Transactional(propagation = Propagation.NESTED)
    public boolean createOrder(int userId, Integer itemId, Integer amount, Integer promoId, String stockLogId) throws InterruptedException {
        Item item = itemService.getItemById(itemId);
        Promo promo = promoService.getPromoById(promoId);
        if (promo == null) {
            throw new RuntimeException("活动未开始");
        }
        if (!itemStockService.decreaseStockInRedis(itemId, amount, promoId)) {
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
            if (true) throw new RuntimeException("go");
            return true;
        } else {
            throw new RuntimeException("下单失败");
        }
    }
}
