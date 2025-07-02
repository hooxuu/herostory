package cc.xuhao.herostory.async;

import cc.xuhao.herostory.SingletonThreadProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/30 21:18
 */
@Slf4j
public final class AsyncOperationProcessor {

    private ExecutorService[] executorArr = new ExecutorService[8];

    private static final AsyncOperationProcessor INSTANCE = new AsyncOperationProcessor();

    public static AsyncOperationProcessor instance() {
        return INSTANCE;
    }

    private AsyncOperationProcessor() {
        for (int i = 0; i < 8; i++) {
            int finalI = i;
            executorArr[i] = Executors.newSingleThreadExecutor((r) -> {
                Thread thread = new Thread(r);
                thread.setName("asyncOperationProcessor_" + finalI);
                return thread;
            });
        }
    }

    public void process(IAsyncOperation operation) {

        if (operation == null) {
            return;
        }

        int bindId = operation.bindId();
        int index = bindId % executorArr.length;
        executorArr[index].submit(() -> {
            try {
                // 异步执行IO操作
                operation.doAsync();

                // 主业务线程执行业务
                SingletonThreadProcessor.instance().process(operation::doFinish);
            } catch (Exception e) {
                log.error("process async operation error", e);
            }
        });
    }

}
