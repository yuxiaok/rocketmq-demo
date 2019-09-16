package com.yk.pull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.consumer.PullStatus;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * @author: yukai
 * @date: 2019-09-16
 **/
public class PullConsumer {

    /**
     * 用来存储每个队列的偏移位置 offset氛围本地存储和远程存储：
     * 集群模型：远程存储，因为集群模型，所有的consumer都是从broker拉取，负载均衡的，所以offset需要放在远程，一边每个consumer去msg的位置是正确的。
     * 广播模型：本地存储，因为广播模型，每个consume都获得是完整的msg,每次消费，肯定是要记录本地上次消费的offset，如果从远程获取，每个offset都不一样，会出问题。
     * 说白了offset，集群模型就是共享，因为msg也是共享，广播模型就是私有，本身获取到的就是完整msg。
     */
    private static final Map<MessageQueue, Long> map = new HashMap<>();

    public static void main(String[] args)
        throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        // 一般都不会使用pull模式，因为太麻烦，rocketmq对push已经封装好了，所以一般情况下都是使用push方式，虽然push本质是pull的长轮询方式
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("pull_consumer");
        consumer.setNamesrvAddr("10.211.55.3:9876;10.211.55.6:9876");
        // push的情况下是长链接，所以需要最后start，并保持长链接
        consumer.start();
        // pull主动拉取消息队列，push情况下，rocketmq已经封装好了
        Set<MessageQueue> messageQueueSet = consumer.fetchSubscribeMessageQueues("Topic_pull");

        for (MessageQueue queue : messageQueueSet) {
            SINGLE_MQ:
            while (true) {
                // 拉取队列中的消息，参数：队列，tag过滤，偏移量，拉取消息最大数量
                PullResult pullResult = consumer.pullBlockIfNotFound(queue, null, getOffset(queue), 32);
                // 更新偏移量
                map.put(queue, pullResult.getNextBeginOffset());
                // 根据返回消息状态，判断执行的业务逻辑
                PullStatus status = pullResult.getPullStatus();
                switch (status) {
                    case FOUND:
                        List<MessageExt> msgFoundList = pullResult.getMsgFoundList();
                        for (MessageExt msg : msgFoundList) {
                            System.out.println("队列：" + queue + "获取的消息：" + new String(msg.getBody()));
                        }
                        break;
                    case NO_NEW_MSG:
                        System.out.println("没有新消息");
                        break SINGLE_MQ;
                    case NO_MATCHED_MSG:
                        System.out.println("没找到匹配的消息");
                        break;
                    case OFFSET_ILLEGAL:
                        System.out.println("偏移量异常");
                        break;
                }
            }
        }
        // 关闭
        consumer.shutdown();
    }

    private static long getOffset(MessageQueue queue) {
        Long aLong = map.get(queue);
        if (aLong == null) {
            return 0L;
        }
        return aLong;
    }
}
