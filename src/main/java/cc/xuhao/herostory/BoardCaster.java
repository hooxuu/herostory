package cc.xuhao.herostory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/26 09:22
 */
public final class BoardCaster {

    private static final ChannelGroup CLIENT_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private BoardCaster() {}

    public static void addChannel(Channel channel) {
        CLIENT_GROUP.add(channel);
    }

    public static boolean removeChannel(Channel channel) {
        return CLIENT_GROUP.remove(channel);
    }


    public static void broadcast(Object msg) {

        if (msg == null) {
            return;
        }

        CLIENT_GROUP.writeAndFlush(msg);
    }
}
