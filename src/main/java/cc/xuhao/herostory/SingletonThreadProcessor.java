package cc.xuhao.herostory;

import cc.xuhao.herostory.handler.CmdHandlerFactory;
import cc.xuhao.herostory.handler.ICmdHandler;
import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/30 11:54
 */
@Slf4j
public final class SingletonThreadProcessor {

    private final static ExecutorService EXECUTOR = Executors.newSingleThreadExecutor((r) -> {
        Thread thread = new Thread(r);
        thread.setName("singletonThreadProcessor");
        return thread;
    });

    private static final SingletonThreadProcessor INSTANCE = new SingletonThreadProcessor();

    private SingletonThreadProcessor() {}

    public static SingletonThreadProcessor instance() {
        return INSTANCE;
    }

    public void process(ChannelHandlerContext ctx, Object msg) {
        if (ctx == null || msg == null) {
            return;
        }

        ICmdHandler<? extends GeneratedMessageV3> handler = CmdHandlerFactory.create(msg.getClass());

        if (handler == null) {
           log.error("no handler for msg {}", msg);
            return;
        }

        EXECUTOR.submit(() -> {
            try {
                handler.handle(ctx, cast(msg));
            } catch (Exception e) {
                log.error("process msg error", e);
            }
        });
    }

    public void process(Runnable runnable) {
        if (runnable == null) {
            return;
        }

        EXECUTOR.submit(runnable);
    }


    @SuppressWarnings("unchecked")
    private <ICmd extends GeneratedMessageV3> ICmd cast(Object msg) {
        if (msg == null) {
            return null;
        }
        return (ICmd) msg;
    }
}
