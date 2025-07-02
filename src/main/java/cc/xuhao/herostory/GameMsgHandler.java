package cc.xuhao.herostory;

import cc.xuhao.herostory.entity.UserManger;
import cc.xuhao.herostory.handler.CmdHandlerFactory;
import cc.xuhao.herostory.handler.ICmdHandler;
import cc.xuhao.herostory.msg.GameMsgProtocol;
import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/25 10:42
 */
@Slf4j
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        BoardCaster.addChannel(ctx.channel());
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (null == ctx) {
            return;
        }

        super.handlerRemoved(ctx);
        BoardCaster.removeChannel(ctx.channel());

        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf(UserManger.USER_ID)).get();
        if (userId == null) {
            return;
        }
        UserManger.removeUser(userId);
        userQuit(userId);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        SingletonThreadProcessor.instance().process(ctx, msg);
    }

    private void userQuit(Integer userId) {
        GameMsgProtocol.UserQuitResult.Builder quitResultBuilder = GameMsgProtocol.UserQuitResult.newBuilder();
        quitResultBuilder.setQuitUserId(userId);
        GameMsgProtocol.UserQuitResult quitResult = quitResultBuilder.build();
        BoardCaster.broadcast(quitResult);
        log.info("user {} quit", userId);
    }
}
