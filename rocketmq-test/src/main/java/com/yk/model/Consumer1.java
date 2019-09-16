package com.yk.model;

import java.util.List;
import java.util.Objects;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * @author: yukai
 * @date: 2019-09-15
 **/
public class Consumer1 {

    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("model_consumer");
        consumer.setNamesrvAddr("10.211.55.3:9876;10.211.55.6:9876");
        // 消费模式：clustering:集群消费，broadcasting：广播消费
        consumer.setMessageModel(MessageModel.CLUSTERING);
        // consumer.setMessageModel(MessageModel.BROADCASTING);
        consumer.subscribe("TopicA", "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                try {
                    for (MessageExt msg : msgs) {
                        byte[] body = msg.getBody();
                        String tags = msg.getTags();
                        if (Objects.equals("TagA", tags)) {
                            System.out.println("tag:" + tags + ",msg:" + new String(body));
                        }
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                } catch (Exception e) {
                    e.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
        });
        consumer.start();
    }
}
