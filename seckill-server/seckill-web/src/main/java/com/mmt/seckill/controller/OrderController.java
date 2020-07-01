package com.mmt.seckill.controller;

import com.mmt.seckill.model.Item;
import com.mmt.seckill.model.Promo;
import com.mmt.seckill.service.*;
import com.mmt.seckill.service.rocketMQ.MqProducer;
import com.mmt.seckill.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
    @Autowired
    private MqProducer mqProducer;
    @Autowired
    private StockLogService stockLogService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private PromoService promoService;
    @Autowired
    private UserService userService;

    @PostMapping("/itemToken")
    public String createPromoToken(@RequestParam(name = "userId") Integer userId,
                                   @RequestParam(name = "itemId") Integer itemId,
                                   @RequestParam(name = "promoId", required = false) Integer promoId) {
        if (userService.selectById(userId) == null) {
            throw new RuntimeException("用户不存在");
        }
        String promoToken = promoService.createPromoToken(promoId, itemId, userId);
        if (promoToken == null) {
            throw new RuntimeException("抢购失败");
        }
        return promoToken;
    }

    @PostMapping("/order")
    public RespBean createOrder(@RequestParam(name = "userId") Integer userId,
                                @RequestParam(name = "itemId") Integer itemId,
                                @RequestParam(name = "amount") Integer amount,
                                @RequestParam(name = "promoId", required = false) Integer promoId,
                                @RequestParam(name = "promoToken", required = false) String promoToken) {
        String redisToken = (String) redisTemplate.opsForValue().get("promo" + promoId + "_item" + itemId + "_user" + userId + "_token");
        if (redisToken == null) {
            throw new RuntimeException("令牌检测失败");
        }
        if (!redisToken.equals(promoToken)) {
            throw new RuntimeException("令牌检测失败");
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
