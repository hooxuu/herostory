package cc.xuhao.herostory.handler;

import cc.xuhao.herostory.BoardCaster;
import cc.xuhao.herostory.entity.MoveState;
import cc.xuhao.herostory.entity.User;
import cc.xuhao.herostory.entity.UserManger;
import cc.xuhao.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/26 09:45
 */
public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd>{
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd userMoveToCmd) {
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf(UserManger.USER_ID)).get();
        if (userId == null) {
            return;
        }

        User user = UserManger.getUserByUserId(userId);
        if (user == null) {
            return;
        }
        MoveState moveState = user.moveState;

        float moveToPosX = userMoveToCmd.getMoveToPosX();
        float moveToPosY = userMoveToCmd.getMoveToPosY();
        float moveFromPosX = userMoveToCmd.getMoveFromPosX();
        float moveFromPosY = userMoveToCmd.getMoveFromPosY();
        // MoveStartTime 时间不能用客户端的，统一采用服务端时间
        long moveStartTime = System.currentTimeMillis();

        // 保存用户的移动状态
        moveState.fromPosX = moveFromPosX;
        moveState.fromPosY = moveFromPosY;
        moveState.toPosX = moveToPosX;
        moveState.toPosY = moveToPosY;
        moveState.startTime = moveStartTime;

        // 构造moveToResult
        GameMsgProtocol.UserMoveToResult.Builder moveToResultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
        moveToResultBuilder.setMoveUserId(userId);
        moveToResultBuilder.setMoveToPosX(moveToPosX);
        moveToResultBuilder.setMoveToPosY(moveToPosY);

        moveToResultBuilder.setMoveFromPosX(moveFromPosX);
        moveToResultBuilder.setMoveFromPosY(moveFromPosY);
        moveToResultBuilder.setMoveStartTime(moveStartTime);

        GameMsgProtocol.UserMoveToResult moveToResult = moveToResultBuilder.build();
        // 广播给所有人
        BoardCaster.broadcast(moveToResult);
    }
}
