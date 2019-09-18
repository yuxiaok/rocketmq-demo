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
        //常用api
        //发送消息时，自动创建服务器不存在的topic
        //producer.setCreateTopicKey("topic");
        //设置默认的消息队列数量，默认为4
        //producer.setDefaultTopicQueueNums(4);
        //设置达到多少字节，进行压缩，默认达到1024*4，进行压缩
        // producer.setCompressMsgBodyOverHowmuch(4096);
        //设置消息发送超时时间，单位毫秒，默3*1000
        //producer.setSendMsgTimeout(3000);
        //如果发送消费返回的不是ok,则重新发送到另一个boker，默认false
        //producer.setRetryAnotherBrokerWhenNotStoreOK(false);
        //设置msg最大字节
        //producer.setMaxMessageSize(1024);
        //同步发送，设置重试次数
        //producer.setRetryTimesWhenSendFailed(1);
        // 3.启动生产者
        producer.start();
        for (int i = 0; i < 10; i++) {
            String tags = i % 2 == 0 ? "TagA" : "TagB";
            // 4.设置消息
            // byte[] b = new byte[2048];
            Message message = new Message("TopicA", tags, "Keys", ("hello rocketmq" + i).getBytes());
            // 5.发送消息
            SendResult sendResult = producer.send(message);
            System.out.println("发送消息，返回结果：" + sendResult.toString());
            //Thread.sleep(5000);
        }
        // 6.关闭
        producer.shutdown();

    }
}
