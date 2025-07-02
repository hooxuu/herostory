package cc.xuhao.herostory.handler;

import cc.xuhao.herostory.BoardCaster;
import cc.xuhao.herostory.entity.User;
import cc.xuhao.herostory.entity.UserManger;
import cc.xuhao.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/26 09:39
 */
@Slf4j
public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd userEntryCmd) {
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf(UserManger.USER_ID)).get();
        if (userId == null) {
            log.error("UserEntryCmdHandler userId is null");
            return;
        }

        User existUser = UserManger.getUserByUserId(userId);
        if (existUser == null) {
            log.error("get user by userId return null, userId = {}", userId);
            return;
        }

        // 构造entryResult
        GameMsgProtocol.UserEntryResult.Builder entryResultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
        entryResultBuilder.setUserId(existUser.id);
        entryResultBuilder.setUserName(existUser.userName);
        entryResultBuilder.setHeroAvatar(existUser.heroAvatar);

        GameMsgProtocol.UserEntryResult entryResult = entryResultBuilder.build();
        BoardCaster.broadcast(entryResult);
    }
}
