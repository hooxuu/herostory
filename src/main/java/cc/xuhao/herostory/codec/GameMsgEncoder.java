package cc.xuhao.herostory.codec;

import cc.xuhao.herostory.GameMsgRecognizer;
import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/25 14:52
 */
@Slf4j
public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        if (ctx == null || msg == null) {
            return;
        }

        if (!(msg instanceof GeneratedMessageV3)) {
            ctx.writeAndFlush(msg);
            return;
        }

        int type = GameMsgRecognizer.getMsgType(msg);
        if (type == -1) {
            log.error("invalid msg, can not recognized: {}", msg);
            return;
        }

        byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();
        ByteBuf content = ctx.alloc().buffer();
        content.writeShort(msgBody.length);
        content.writeShort(type);
        content.writeBytes(msgBody);

        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(content);
        super.write(ctx, frame, promise);
    }
}
