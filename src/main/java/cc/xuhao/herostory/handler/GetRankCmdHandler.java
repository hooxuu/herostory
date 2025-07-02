package cc.xuhao.herostory.handler;

import cc.xuhao.herostory.msg.GameMsgProtocol;
import cc.xuhao.herostory.rank.RankItem;
import cc.xuhao.herostory.rank.RankService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/7/2 16:07
 */
@Slf4j
public class GetRankCmdHandler implements ICmdHandler<GameMsgProtocol.GetRankCmd>{
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.GetRankCmd cmd) {
        if (null == ctx || null == cmd) {
            return;
        }

        RankService.getInstance().getRank(rankItems -> {
            GameMsgProtocol.GetRankResult.Builder resultBuilder = GameMsgProtocol.GetRankResult.newBuilder();

            for (RankItem rankItem : rankItems) {
                GameMsgProtocol.GetRankResult.RankItem.Builder rankItemBuilder = GameMsgProtocol.GetRankResult.RankItem.newBuilder();
                rankItemBuilder.setRankId(rankItem.rankId);
                rankItemBuilder.setUserId(rankItem.userId);
                rankItemBuilder.setUserName(rankItem.userName);
                rankItemBuilder.setHeroAvatar(rankItem.heroAvatar);
                rankItemBuilder.setWin(rankItem.win);
                GameMsgProtocol.GetRankResult.RankItem newRankItem = rankItemBuilder.build();
                resultBuilder.addRankItem(newRankItem);
            }
            GameMsgProtocol.GetRankResult result = resultBuilder.build();
            ctx.writeAndFlush(result);
            return null;
        });
    }
}
