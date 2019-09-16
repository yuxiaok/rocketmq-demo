package com.yk.test;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * @author: yukai
 * @date: 2019-09-11
 **/
public class Consumer {

    public static void main(String[] args) throws MQClientException {
        // 1.创建消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("quickstart_consumer");
        // 2.设置namesrv地址
        consumer.setNamesrvAddr("10.211.55.3:9876;10.211.55.6:9876");
        // 一些常用api
        // 消费模型：集群消费，广播消费
        consumer.setMessageModel(MessageModel.CLUSTERING);
        // 线程数量
        consumer.setConsumeThreadMax(20);
        consumer.setConsumeThreadMin(10);
        // 第一次启动后，从哪个位置开始消费，默认队列先进先出
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        // 3.订阅主题，一个consume可以订阅多个主题，使用subscribe方法订阅,subExpression参数是通过tag过滤，如果不过滤，使用*
        consumer.subscribe("TopicA", "*");
        // 4.注册监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                try {
                    // 这里进行业务逻辑操作
                    for (MessageExt msg : msgs) {
                        int queueId = msg.getQueueId();
                        byte[] body = msg.getBody();
                        System.out
                            .println("消费，queueId:" + queueId + ",body:" + new String(body) + ",msg:" + msg.toString());
                    }
                    // 返回成功
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                } catch (Exception e) {
                    // 如果报异常了，则重试
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
        });
        // 5.启动消费者
        consumer.start();
        // 6.关闭,这里不能shutdown，会导致消费不了
        // consumer.shutdown();
    }
}
