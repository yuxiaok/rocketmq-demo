package com.yk.tryagain;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

/**
 * @author: yukai
 * @date: 2019-09-16
 **/
public class Consumer1 {

    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("tryagain_consumer");
        consumer.setNamesrvAddr("10.211.55.3:9876;10.211.55.6:9876");
        consumer.subscribe("TopicTryAgain", "*");
        //设置重试次数
        consumer.setMaxReconsumeTimes(3);
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) throws UnsupportedEncodingException {
                MessageExt messageExt = msgs.get(0);
                String msg = new String(messageExt.getBody(), "utf-8");
                try {
                    System.out.println("消费的消息：" + msg);
                    int i = 10 / 0;
                } catch (Exception e) {
                    //通过判断重试次数，来记录日志，并跳过该条消息，不再进行重试
                    System.out.println("消息异常，记录日志，重试，次数：" + messageExt.getReconsumeTimes() + ",内容：" + msg + "，时间：" + new Date());
                  /*  if (messageExt.getReconsumeTimes() == 3) {
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }*/
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
    }
}
