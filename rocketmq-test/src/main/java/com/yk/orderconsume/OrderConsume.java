package com.yk.orderconsume;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * @author: yukai
 * @date: 2019-09-18
 **/
public class OrderConsume {

    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("order_consumer");
        consumer.setNamesrvAddr("10.211.55.3:9876;10.211.55.6:9876");
        consumer.subscribe("OrderTopic", "*");
        //顺序消费
        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                try {
                    for (MessageExt msg : msgs
                    ) {
                        System.out.println("msgId:" + msg.getQueueId() + ",msg:" + new java.lang.String(msg.getBody()));
                    }
                    return ConsumeOrderlyStatus.SUCCESS;
                } catch (Exception e) {
                    e.printStackTrace();
                    //重试
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;

                }
            }
        });
        consumer.start();
    }
}
