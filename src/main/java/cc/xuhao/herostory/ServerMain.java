package cc.xuhao.herostory;

import cc.xuhao.herostory.codec.GameMsgDecoder;
import cc.xuhao.herostory.codec.GameMsgEncoder;
import cc.xuhao.herostory.handler.CmdHandlerFactory;
import cc.xuhao.herostory.mq.MQConsumer;
import cc.xuhao.herostory.mq.MQProducer;
import cc.xuhao.herostory.util.RedisUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/25 09:58
 */
@Slf4j
public class ServerMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(
                            new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    ch.pipeline().addLast(
                                            new HttpServerCodec(),
                                            new HttpObjectAggregator(65535),
                                            new WebSocketServerProtocolHandler("/websocket", 600000L),
                                            new GameMsgDecoder(),
                                            new GameMsgEncoder(),
                                            new GameMsgHandler()
                                    );
                                }
                            }
                    );
            ChannelFuture f = b.bind(12345).sync();

            if (f.isSuccess()) {
                CmdHandlerFactory.init();
                GameMsgRecognizer.init();
                DBSessionFactory.init();
                RedisUtil.init();
                MQProducer.init();
                LOGGER.info("server started at port {}", 12345);
            }

            f.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("server start failed", e);
        }
    }
}
