package com.mmt.seckill.service;

import com.mmt.seckill.mapper.StockLogMapper;
import com.mmt.seckill.model.StockLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StockLogService {
    @Autowired
    private StockLogMapper stockLogMapper;

    public String createStockLog(Integer itemId, Integer amount) {
        StockLog stockLog = new StockLog();
        stockLog.setAmount(amount);
        stockLog.setItemId(itemId);
        stockLog.setStatus(0);
        stockLog.setStockLogId(UUID.randomUUID().toString().replace("-", ""));
        stockLogMapper.insert(stockLog);
        return stockLog.getStockLogId();
    }

    public boolean changeStatus(String stockLogId, Integer status) {
        StockLog stockLog = stockLogMapper.selectById(stockLogId);
        stockLog.setStatus(status);
        if (stockLogMapper.updateById(stockLog) == 1) {
            return true;
        } else {
            throw new RuntimeException("修改流水状态失败");
        }
    }
}
