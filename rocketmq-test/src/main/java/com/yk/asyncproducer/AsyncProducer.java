package com.yk.asyncproducer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * @author: yukai
 * @date: 2019-09-17
 **/
public class AsyncProducer {


    public static void main(String[] args) throws RemotingException, MQClientException, InterruptedException {
        DefaultMQProducer producer = new DefaultMQProducer("async_producer");
        producer.setNamesrvAddr("10.211.55.3:9876;10.211.55.6:9876");
        //异步发送，设置重试次数
        //producer.setRetryTimesWhenSendAsyncFailed(2);
        producer.start();
        for (int i = 0; i < 1; i++) {
            Message msg = new Message("TopicAsync", "TagA", "keys", ("async_producer" + i).getBytes());
            //异步发送消息
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println("发送成功：" + sendResult);
                }

                @Override
                public void onException(Throwable e) {
                    System.out.println("发送失败" + e);
                }
            });
        }
        //异步发送，不能关闭
        //producer.shutdown();
    }
}
