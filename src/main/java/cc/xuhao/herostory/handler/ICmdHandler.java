package cc.xuhao.herostory.handler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/26 09:37
 */
public interface ICmdHandler<ICmd extends GeneratedMessageV3> {


    void handle(ChannelHandlerContext ctx, ICmd cmd);
}
