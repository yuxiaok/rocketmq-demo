package com.yk.transaction;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.io.UnsupportedEncodingException;

/**
 * @author: yukai
 * @date: 2019-09-18
 **/
public class TransactionProducer {

    public static void main(String[] args) throws UnsupportedEncodingException, MQClientException {
        TransactionMQProducer producer = new TransactionMQProducer("transaction_producer");
        producer.setNamesrvAddr("10.211.55.3:9876;10.211.55.6:9876");
        producer.setTransactionListener(new TransactionListener() {
            /**
             * 消息发送至mq,可以执行本地事务
             * @param msg Half(prepare) message
             * @param arg Custom business parameter 自定义参数
             * @return
             */
            @Override
            public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
                System.out.println("arg:" + arg);
                System.out.println("开始本地事务执行");
                //第二次返回给mq，事务执行成功
                return LocalTransactionState.COMMIT_MESSAGE;
            }

            /**
             * mq回调函数
             * @param msg Check message
             * @return
             */
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                System.out.println("mq回调检查当前状态");
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });
        producer.start();
        Message msg = new Message("Topic_Transaction", "TagA", "keys", "事务".getBytes("UTF-8"));
        TransactionSendResult result = producer.sendMessageInTransaction(msg, "自定义参数");
        System.out.println("result:" + result);
    }
}
