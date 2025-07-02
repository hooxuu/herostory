package cc.xuhao.herostory.rank;

import cc.xuhao.herostory.async.AsyncOperationProcessor;
import cc.xuhao.herostory.async.IAsyncOperation;
import cc.xuhao.herostory.login.db.UserDO;
import cc.xuhao.herostory.util.RedisUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/7/2 15:20
 */
@Slf4j
public final class RankService {

    private static final RankService INSTANCE = new RankService();

    public static RankService getInstance() {
        return INSTANCE;
    }

    public void getRank(Function<List<RankItem>, Void> callback) {

        AsyncGetRank asyncGetRank = new AsyncGetRank() {
            @Override
            public void doFinish() {
                callback.apply(getRankItem());
            }
        };

        // 其它线程处理I/O
        AsyncOperationProcessor.instance().process(asyncGetRank);
    }

    private static class AsyncGetRank implements IAsyncOperation {
        private List<RankItem> rankItemList;

        @Override
        public void doAsync() {
            try (Jedis jedis = RedisUtil.getJedis()) {
                // 获取字符串集合
                List<Tuple> valList = jedis.zrevrangeWithScores("Rank", 0, 9);

                AtomicInteger rankId = new AtomicInteger(0);
                rankItemList = valList.stream()
                        .map(tuple -> {
                            RankItem item = new RankItem();
                            item.rankId = rankId.incrementAndGet();
                            item.userId = Integer.parseInt(tuple.getElement());

                            String jsonStr = jedis.hget("User_" + item.userId, "BasicInfo");
                            UserDO userDO =JSON.parseObject(jsonStr, UserDO.class);
                            item.userName = userDO.userName;
                            item.heroAvatar = userDO.heroAvatar;
                            item.win = (int) tuple.getScore();
                            return item;
                        }).collect(Collectors.toList());
            } catch (Exception e) {
                log.error("get rank error", e);
            }
        }

        public List<RankItem> getRankItem() {
            return rankItemList;
        }
    }


    public void refreshRank(int winId, int loseId) {
        try (Jedis jedis = RedisUtil.getJedis()) {
            // 写入击败胜利或者被击败次数
            jedis.hincrBy("User_" + winId, "win", 1);
            jedis.hincrBy("User_" + loseId, "lose", 1);

            int winTimes = Integer.parseInt(jedis.hget("User_" + winId, "win"));

            jedis.zadd("Rank", winTimes, String.valueOf(winId));
        } catch (Exception e) {
            log.error("refresh rank error", e);
        }
    }
}
