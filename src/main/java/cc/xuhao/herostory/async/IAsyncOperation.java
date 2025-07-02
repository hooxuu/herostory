package cc.xuhao.herostory.async;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/30 21:16
 */
public interface IAsyncOperation {

    int bindId();


    void doAsync();


    default void doFinish() {}
}
