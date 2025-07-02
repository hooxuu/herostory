package cc.xuhao.herostory.mq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/7/2 16:20
 */
@Slf4j
public final class MQProducer {

    /**
     * 生产者
     */
    private static DefaultMQProducer producer = null;

    /**
     * 私有化类默认构造器
     */
    private MQProducer() {
    }


    public static void init() {
        try {
            producer = new DefaultMQProducer(MQConstants.GROUP_NAME);
            producer.setNamesrvAddr("10.0.0.13:9876");
            producer.setRetryTimesWhenSendAsyncFailed(3);
            producer.start();
            log.info("init mq producer success");
        } catch (Exception e) {
            log.error("init mq producer error", e);
        }
    }

    public static void sendMsg(String topic, Object msg) {
        if (null == producer) {
            throw new IllegalStateException("mq producer not initialized");
        }

        if (null == topic || null == msg) {
            log.error("send mq message error, topic or msg is null");
            return;
        }

        try {
            Message message = new Message();
            message.setTopic(topic);
            message.setBody(JSON.toJSONBytes(msg));
            producer.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
