package com.yk.test;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * @author: yukai
 * @date: 2019-09-11
 **/
public class Producer {

    public static void main(String[] args)
        throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        // 1.创建生产者
        DefaultMQProducer producer = new DefaultMQProducer("quickstart_producer");
        // 2.设置namesrv地址
        producer.setNamesrvAddr("10.211.55.3:9876;10.211.55.6:9876");
        // 3.启动生产者
        producer.start();
        for (int i = 10; i < 20; i++) {
            String tags = i % 2 == 0 ? "TagA" : "TagB";
            // 4.设置消息
            Message message = new Message("TopicA", tags, "Keys", ("hello rocketmq" + i).getBytes());
            // 5.发送消息
            SendResult sendResult = producer.send(message);
            System.out.println("发送消息，返回结果：" + sendResult.toString());
        }
        // 6.关闭
        producer.shutdown();

    }
}
