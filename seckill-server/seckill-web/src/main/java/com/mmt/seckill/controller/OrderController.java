package com.mmt.seckill.controller;

import com.mmt.seckill.service.OrderService;
import com.mmt.seckill.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/")
    public RespBean createOrder(@RequestParam(name = "userId") Integer userId,
                                @RequestParam(name = "itemId") Integer itemId,
                                @RequestParam(name = "amount") Integer amount,
                                @RequestParam(name = "promoId", required = false) Integer promoId) {
        return orderService.createOrder(userId, itemId, amount, promoId);
    }
}
