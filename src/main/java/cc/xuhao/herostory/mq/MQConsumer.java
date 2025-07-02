package cc.xuhao.herostory.mq;

import cc.xuhao.herostory.rank.RankService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/7/2 16:36
 */
@Slf4j
public final class MQConsumer {
    private MQConsumer() {}


    public static void init() {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(MQConstants.GROUP_NAME);
        consumer.setNamesrvAddr("10.0.0.13:9876");
        try {
            consumer.subscribe(MQConstants.TOPIC, "*");
            consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                for (MessageExt msg : msgs) {
                    VictorMsg vMsg = JSON.parseObject(
                            msg.getBody(),
                            VictorMsg.class
                    );

                    log.info("received new msg, winId: {}, loseId: {}", vMsg.winId, vMsg.loseId);

                    RankService.getInstance().refreshRank(vMsg.winId, vMsg.loseId);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });
            consumer.start();
            log.info("init mq consumer success");
        } catch (Exception e) {
            log.error("mq consumer something wrong", e);
        }
    }
}
