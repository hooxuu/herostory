package cc.xuhao.herostory;

import cc.xuhao.herostory.mq.MQConsumer;
import cc.xuhao.herostory.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 结算消息消费端，和主服务相独立
 * @Author: xuhao
 * @Date: 2025/7/2 17:02
 */
@Slf4j
public class RankMain {

    public static void main(String[] args) {
        RedisUtil.init();
        MQConsumer.init();

        log.info("rank service started");
    }
}
