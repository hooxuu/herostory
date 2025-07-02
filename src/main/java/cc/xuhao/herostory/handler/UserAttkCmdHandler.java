package cc.xuhao.herostory.handler;

import cc.xuhao.herostory.BoardCaster;
import cc.xuhao.herostory.entity.User;
import cc.xuhao.herostory.entity.UserManger;
import cc.xuhao.herostory.mq.MQConstants;
import cc.xuhao.herostory.mq.MQProducer;
import cc.xuhao.herostory.mq.VictorMsg;
import cc.xuhao.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.util.Random;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/27 15:29
 */
public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd cmd) {
        // 受害者
        int targetUserId = cmd.getTargetUserId();
        User targetUser = UserManger.getUserByUserId(targetUserId);

        if (targetUser == null) {
            return;
        }

        if (targetUser.hp <= 0) {
            return;
        }

        Integer attacker = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (attacker == null) {
            return;
        }

        boardCastSubtractHp(attacker, targetUser);
        // 检查是否死亡
        if (targetUser.hp <= 0) {
            boardCastDie(targetUserId);

            // 给消息队列写攻击结算消息
            VictorMsg msg = new VictorMsg();
            msg.winId = attacker;
            msg.loseId = targetUserId;
            MQProducer.sendMsg(MQConstants.TOPIC, msg);
        }
    }

    private void boardCastSubtractHp(int attacker, User targetUser) {
        GameMsgProtocol.UserAttkResult.Builder attkResultBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        attkResultBuilder.setAttkUserId(attacker);
        attkResultBuilder.setTargetUserId(targetUser.id);
        GameMsgProtocol.UserAttkResult attkResult = attkResultBuilder.build();
        BoardCaster.broadcast(attkResult);

        // 扣血
        Random random = new Random();
        int subtractHp = random.nextInt(20);
        targetUser.hp -= subtractHp;

        GameMsgProtocol.UserSubtractHpResult.Builder subHpResultBuilder = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        subHpResultBuilder.setTargetUserId(targetUser.id);
        subHpResultBuilder.setSubtractHp(subtractHp);
        GameMsgProtocol.UserSubtractHpResult subHpResult = subHpResultBuilder.build();
        BoardCaster.broadcast(subHpResult);
    }

    private void boardCastDie(int targetUserId) {
        GameMsgProtocol.UserDieResult.Builder dieResultBuilder = GameMsgProtocol.UserDieResult.newBuilder();
        dieResultBuilder.setTargetUserId(targetUserId);
        GameMsgProtocol.UserDieResult dieResult = dieResultBuilder.build();
        BoardCaster.broadcast(dieResult);
    }
}
