package cc.xuhao.herostory.codec;

import cc.xuhao.herostory.GameMsgRecognizer;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/25 11:26
 */
@Slf4j
public class GameMsgDecoder extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (!(msg instanceof BinaryWebSocketFrame)) {
            return;
        }

        BinaryWebSocketFrame frame = (BinaryWebSocketFrame) msg;
        ByteBuf content = frame.content();
        content.readShort();
        short type = content.readShort();
        byte[] buffer = new byte[content.readableBytes()];
        content.readBytes(buffer);

        Message.Builder builder = GameMsgRecognizer.getMsgBuilderByType(type);
        if (builder == null) {
            log.error("invalid msg type, {}", type);
            return;
        }

        Message newMsg = builder.mergeFrom(buffer).build();
        ctx.fireChannelRead(newMsg);
    }
}
