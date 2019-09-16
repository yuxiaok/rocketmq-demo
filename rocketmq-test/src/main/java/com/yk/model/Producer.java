package com.yk.model;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * @author: yukai
 * @date: 2019-09-15
 **/
public class Producer {

    public static void main(String[] args)
        throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        DefaultMQProducer producer = new DefaultMQProducer("model_producer");
        producer.setNamesrvAddr("10.211.55.3:9876;10.221.55.6:9876");
        producer.start();
        for (int i = 0; i < 10; i++) {
            String tag = i % 2 == 0 ? "TagA" : "TagB";
            Message message = new Message("TopicA", tag, "Keys", ("rocketmq-model" + i).getBytes());
            String tags = message.getTags();
            SendResult sendResult = producer.send(message);
            System.out.println(tags + "," + sendResult);
        }
        producer.shutdown();
    }
}
