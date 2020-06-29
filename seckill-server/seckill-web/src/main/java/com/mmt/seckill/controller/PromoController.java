package com.mmt.seckill.controller;

import com.mmt.seckill.model.Promo;
import com.mmt.seckill.service.PromoService;
import com.mmt.seckill.utils.RespBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@RestController
@RequestMapping("/promo")
public class PromoController {
    @Autowired
    private PromoService promoService;

    @PostMapping("/")
    public RespBean createPromo(@RequestParam("item_id") int itemId, @RequestParam("promoItemPrice") double promoItemPrice) {
        Promo promo = new Promo();
        promo.setStartDate(new Timestamp(2020, 6, 29, 22, 15, 0, 0));
        promo.setEndDate(new Timestamp(2020, 6, 29, 22, 20, 0, 0));
        promo.setItemId(itemId);
        promo.setPromoItemPrice(promoItemPrice);
        promo.setPromoName("抢购活动" + itemId);
        return promoService.createPromo(promo);
    }
}
