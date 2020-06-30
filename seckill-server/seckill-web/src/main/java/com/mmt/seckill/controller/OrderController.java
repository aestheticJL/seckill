package com.mmt.seckill.controller;

import com.mmt.seckill.service.StockLogService;
import com.mmt.seckill.service.rocketMQ.MqProducer;
import com.mmt.seckill.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private MqProducer mqProducer;
    @Autowired
    private StockLogService stockLogService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/")
    public RespBean createOrder(@RequestParam(name = "userId") Integer userId,
                                @RequestParam(name = "itemId") Integer itemId,
                                @RequestParam(name = "amount") Integer amount,
                                @RequestParam(name = "promoId", required = false) Integer promoId) {
        //判断库存是否售罄
        if (redisTemplate.hasKey("promo_item_stock_invalid" + itemId)) {
            return RespBean.error("库存不足");
        }
        //创建订单流水
        String stockLogId = stockLogService.createStockLog(itemId, amount);
        //发送一个事务型消息给消息队列
        if (mqProducer.transactionAsyncReduceStock(userId, itemId, amount, promoId, stockLogId)) {
            return RespBean.ok("下单成功");
        } else {
            return RespBean.error("下单失败");
        }
    }
}
