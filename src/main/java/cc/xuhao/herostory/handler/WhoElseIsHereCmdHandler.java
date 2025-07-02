package cc.xuhao.herostory.handler;

import cc.xuhao.herostory.entity.UserManger;
import cc.xuhao.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/26 09:44
 */
public class WhoElseIsHereCmdHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd>{
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd whoElseIsHereCmd) {
        // 构造whoElseIsHereResult
        GameMsgProtocol.WhoElseIsHereResult.Builder whoElseIsHereResultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

        UserManger.listAllUser().forEach(user -> {
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
            userBuilder.setUserId(user.id);
            userBuilder.setUserName(user.userName);
            userBuilder.setHeroAvatar(user.heroAvatar);

            GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder moveStateBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.newBuilder();
            moveStateBuilder.setFromPosX(user.moveState.fromPosX);
            moveStateBuilder.setFromPosY(user.moveState.fromPosY);
            moveStateBuilder.setToPosX(user.moveState.toPosX);
            moveStateBuilder.setToPosY(user.moveState.toPosY);
            moveStateBuilder.setStartTime(user.moveState.startTime);
            userBuilder.setMoveState(moveStateBuilder);
            whoElseIsHereResultBuilder.addUserInfo(userBuilder);
        });

        GameMsgProtocol.WhoElseIsHereResult whoElseIsHereResult = whoElseIsHereResultBuilder.build();
        // 只发给自己
        ctx.writeAndFlush(whoElseIsHereResult);
    }
}
