package com.mmt.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mmt.seckill.model.ItemStock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ItemStockMapper extends BaseMapper<ItemStock> {
    boolean decreaseStockInMySQL(@Param("itemId") Integer itemId, @Param("amount") Integer amount);

    int getItemStockByItemId(int id);
}
