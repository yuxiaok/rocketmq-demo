package com.yk.orderconsume;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.List;

/**
 * @author: yukai
 * @date: 2019-09-18
 **/
public class OrderProducer {

    public static void main(String[] args) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        DefaultMQProducer producer = new DefaultMQProducer("order_producer");
        producer.setNamesrvAddr("10.211.55.3:9876;10.211.55.6:9876");
        producer.start();
        for (int i = 0; i < 5; i++) {
            Message message = new Message("OrderTopic", "TagA", "Keys", ("order" + i).getBytes());
            producer.send(message, new MessageQueueSelector() {
                /**
                 *
                 * @param mqs 消息队列集合
                 * @param msg 消息
                 * @param arg 自定义参数
                 * @return
                 */
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    System.out.println("mqs" + mqs.toString() + "msg:" + msg + ",arg:" + arg);
                    //选取一个队列，这个index可以任意选取
                    return mqs.get(1);
                }
            }, "自定义参数");
        }
        for (int i = 0; i < 5; i++) {
            Message message = new Message("OrderTopic", "TagA", "Keys", ("order" + i).getBytes());
            producer.send(message, new MessageQueueSelector() {
                /**
                 *
                 * @param mqs 消息队列集合
                 * @param msg 消息
                 * @param arg 自定义参数
                 * @return
                 */
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    System.out.println("mqs" + mqs.toString() + "msg:" + msg + ",arg:" + arg);
                    //选取一个队列，这个index可以任意选取
                    return mqs.get(2);
                }
            }, "自定义参数2");
        }
        for (int i = 0; i < 5; i++) {
            Message message = new Message("OrderTopic", "TagA", "Keys", ("order" + i).getBytes());
            producer.send(message, new MessageQueueSelector() {
                /**
                 *
                 * @param mqs 消息队列集合
                 * @param msg 消息
                 * @param arg 自定义参数
                 * @return
                 */
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    System.out.println("mqs" + mqs.toString() + "msg:" + msg + ",arg:" + arg);
                    //选取一个队列，这个index可以任意选取
                    return mqs.get(0);
                }
            }, "自定义参数0");
        }
        producer.shutdown();
    }
}
