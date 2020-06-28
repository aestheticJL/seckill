package com.mmt.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mmt.seckill.model.Promo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface PromoMapper extends BaseMapper<Promo> {
    @Select("SELECT promo_item_price FROM promo WHERE id = #{promoId} ")
    double selectPromoItemPriceById(Integer id);
}
