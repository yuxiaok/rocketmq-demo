package com.yk.tryagain;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * @author: yukai
 * @date: 2019-09-16
 **/
public class Producer {

    public static void main(String[] args) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        DefaultMQProducer producer = new DefaultMQProducer("tryagain_producer");
        producer.setNamesrvAddr("10.211.55.3:9876;10.211.55.6:9876");
        producer.start();
        for (int i = 0; i < 3; i++) {
            Message message = new Message("TopicTryAgain", "TagA", "keys", ("tryagain" + i).getBytes());
            SendResult sendResult = producer.send(message);
            System.out.println(sendResult);
        }
        producer.shutdown();
    }
}
