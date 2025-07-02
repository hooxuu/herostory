package cc.xuhao.herostory.async;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/30 21:16
 */
public interface IAsyncOperation {

    default int bindId() {
        return 0;
    }


    void doAsync();


    default void doFinish() {}
}
