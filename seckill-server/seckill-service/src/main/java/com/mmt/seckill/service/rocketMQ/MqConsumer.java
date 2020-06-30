package com.mmt.seckill.service.rocketMQ;

import com.alibaba.fastjson.JSON;
import com.mmt.seckill.mapper.ItemStockMapper;
import com.mmt.seckill.service.ItemStockService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Component
public class MqConsumer {
    @Autowired
    private ItemStockService itemStockService;

    private DefaultMQPushConsumer consumer;

    @Value("${mq.nameserver.addr}")
    private String nameAddress;

    @Value("${mq.topicname}")
    private String topicName;

    @PostConstruct
    public void init() throws MQClientException {
        consumer = new DefaultMQPushConsumer("stock_consumer_group");
        consumer.setNamesrvAddr(nameAddress);
        consumer.subscribe(topicName, "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgList, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                //消费，扣取mysql库存
                Message message = msgList.get(0);
                String jsonMessage = new String(message.getBody());
                Map<String, Object> map = JSON.parseObject(jsonMessage, Map.class);
                int itemId = (int) map.get("itemId");
                int amount = (int) map.get("amount");
                itemStockService.decreaseStockInMySQL(itemId, amount);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
    }
}
