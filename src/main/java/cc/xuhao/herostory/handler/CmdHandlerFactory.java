package cc.xuhao.herostory.handler;

import cc.xuhao.herostory.util.PackageUtil;
import com.google.protobuf.GeneratedMessageV3;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/26 10:05
 */
@Slf4j
public final class CmdHandlerFactory {
    private static final Map<Class<?>, ICmdHandler<? extends GeneratedMessageV3>> CMD_HANDLER_MAP = new HashMap<>();

    private static final String HANDLE_METHOD_NAME = "handle";

    private CmdHandlerFactory() {}

    public static void init() {
        String packageName = CmdHandlerFactory.class.getPackage().getName();
        Set<Class<?>> subClazzSet = PackageUtil.listSubClazz(
                packageName,
                true,
                ICmdHandler.class
        );

        for (Class<?> subClz : subClazzSet) {
            // 排除抽象类
            if ((subClz.getModifiers() & Modifier.ABSTRACT) != 0) {
                continue;
            }

            Method[] methods = subClz.getDeclaredMethods();
            // 消息类型
            Class<?> msgType = null;

            for (Method m : methods) {
                // 不是handle方法
                if (!HANDLE_METHOD_NAME.equals(m.getName())) {
                    continue;
                }

                Class<?>[] types = m.getParameterTypes();
                if (types.length != 2
                        || !GeneratedMessageV3.class.isAssignableFrom(types[1])) {
                    continue;
                }

                msgType = types[1];
                break;
            }

            if (msgType == null) {
                continue;
            }

            try {
                // 创建handler
                ICmdHandler<?> handler = (ICmdHandler<?>) subClz.newInstance();
                log.info(
                        "关联 {} <==> {}",
                        msgType.getName(),
                        subClz.getName()
                );

                CMD_HANDLER_MAP.put(msgType, handler);
            } catch (Exception e) {
                log.error("创建handler失败", e);
            }
        }
    }

    public static ICmdHandler<? extends GeneratedMessageV3> create(Class<?> cmdClass) {
        return CMD_HANDLER_MAP.get(cmdClass);
    }
}
