package com.mmt.seckill.service.rocketMQ;

import com.alibaba.fastjson.JSON;
import com.mmt.seckill.mapper.StockLogMapper;
import com.mmt.seckill.model.StockLog;
import com.mmt.seckill.service.OrderService;
import com.mmt.seckill.service.StockLogService;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Component
public class MqProducer {
    @Autowired
    private OrderService orderService;

    @Autowired
    private StockLogMapper stockLogMapper;

    @Autowired
    private StockLogService stockLogService;

    private DefaultMQProducer producer;

    private TransactionMQProducer transactionMQProducer;

    @Value("${mq.nameserver.addr}")
    private String nameAddress;

    @Value("${mq.topicname}")
    private String topicName;

    @PostConstruct
    public void init() throws MQClientException {
        producer = new DefaultMQProducer("producer_group");
        producer.setNamesrvAddr(nameAddress);
        producer.start();
        transactionMQProducer = new TransactionMQProducer("transaction_producer_group");
        transactionMQProducer.setNamesrvAddr(nameAddress);
        transactionMQProducer.start();
        transactionMQProducer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object argsMap) {
                Integer userId = (Integer) ((Map<String, Object>) argsMap).get("userId");
                Integer itemId = (Integer) ((Map<String, Object>) argsMap).get("itemId");
                Integer amount = (Integer) ((Map<String, Object>) argsMap).get("amount");
                Integer promoId = (Integer) ((Map<String, Object>) argsMap).get("promoId");
                String stockLogId = (String) ((Map<String, Object>) argsMap).get("stockLogId");
                try {
                    orderService.createOrder(userId, itemId, amount, promoId, stockLogId);
                } catch (Exception e) {
                    stockLogService.changeStatus(stockLogId, -1);
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.COMMIT_MESSAGE;

            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                String jsonMessage = new String(messageExt.getBody());
                Map<String, Object> map = JSON.parseObject(jsonMessage, Map.class);
                String stockLogId = (String) map.get("stockLogId");
                StockLog stockLog = stockLogMapper.selectById(stockLogId);
                if (stockLog == null) {
                    return LocalTransactionState.UNKNOW;
                }
                if (stockLog.getStatus() == 1) {
                    return LocalTransactionState.COMMIT_MESSAGE;
                }
                if (stockLog.getStatus() == 0) {
                    return LocalTransactionState.UNKNOW;
                }
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        });
    }

    public boolean transactionAsyncReduceStock(Integer userId, Integer itemId, Integer amount, Integer promoId, String stockLogId) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("itemId", itemId);
        bodyMap.put("amount", amount);
        bodyMap.put("stockLogId", stockLogId);
        Map<String, Object> argsMap = new HashMap<>();
        argsMap.put("userId", userId);
        argsMap.put("itemId", itemId);
        argsMap.put("amount", amount);
        argsMap.put("promoId", promoId);
        argsMap.put("stockLogId", stockLogId);
        Message message = new Message(topicName, "increase", JSON.toJSON(bodyMap).toString().getBytes(Charset.forName("UTF-8")));
        TransactionSendResult transactionSendResult;
        try {
            //发送消息，将在消费里面处理mysql的减库存
            transactionSendResult = transactionMQProducer.sendMessageInTransaction(message, argsMap);
        } catch (MQClientException e) {
            return false;
        }
        if (transactionSendResult.getLocalTransactionState() == LocalTransactionState.COMMIT_MESSAGE) {
            return true;
        } else {
            return false;
        }
    }

    //同步库存消息
//    public boolean asyncReduceStock(Integer itemId, Integer amount) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("itemId", itemId);
//        map.put("amount", amount);
//        Message message = new Message(topicName, "increase", JSON.toJSON(map).toString().getBytes(Charset.forName("UTF-8")));
//        try {
//            producer.send(message);
//        } catch (MQClientException e) {
//            e.printStackTrace();
//            return false;
//        } catch (RemotingException e) {
//            e.printStackTrace();
//            return false;
//        } catch (MQBrokerException e) {
//            e.printStackTrace();
//            return false;
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
}
