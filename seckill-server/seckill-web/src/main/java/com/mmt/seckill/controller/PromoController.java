package com.mmt.seckill.controller;

import com.mmt.seckill.model.Promo;
import com.mmt.seckill.service.PromoService;
import com.mmt.seckill.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.sql.Timestamp;
import java.util.Date;

@RestController
@RequestMapping("/promo")
public class PromoController {
    @Autowired
    private PromoService promoService;

    @PostMapping("/")
    public RespBean createPromo(@RequestParam("item_id") int itemId, @RequestParam("promoItemPrice") double promoItemPrice) {
        Promo promo = new Promo();
        Timestamp startDate = new Timestamp(new Date().getTime());
        promo.setStartDate(startDate);
        promo.setEndDate(new Timestamp(startDate.getTime() + 60 * 60));
        promo.setItemId(itemId);
        promo.setPromoItemPrice(promoItemPrice);
        promo.setPromoName("抢购活动" + itemId);
        return promoService.createPromo(promo);
    }
}
