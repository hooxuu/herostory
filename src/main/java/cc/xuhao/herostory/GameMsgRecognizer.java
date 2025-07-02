package cc.xuhao.herostory;

import cc.xuhao.herostory.msg.GameMsgProtocol;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/26 14:02
 */
@Slf4j
public final class GameMsgRecognizer {
    private static final Map<Integer, Message> MSG_TYPE_MAP = new HashMap<>();

    private static final Map<Class<?>, Integer> CMD_TYPE_MAP = new HashMap<>();

    private GameMsgRecognizer() {}

    public static void init() {
        Class<?>[] declaredClasses = GameMsgProtocol.class.getDeclaredClasses();
        for (Class<?> declaredClass : declaredClasses) {
            if (!(GeneratedMessageV3.class.isAssignableFrom(declaredClass))) {
                continue;
            }

            String innerClzName = declaredClass.getSimpleName();
            String lowCaseClz = innerClzName.toLowerCase();

            for (GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()) {
                String name = msgCode.name();
                name = name.replace("_", "");
                name = name.toLowerCase();

                if (!name.startsWith(lowCaseClz)) {
                    continue;
                }

                try {
                    Object returnObj = declaredClass.getDeclaredMethod("getDefaultInstance").invoke(declaredClass);
                    log.info(
                            "关联 {} <==> {}",
                            declaredClass.getName(),
                            msgCode.getNumber()
                    );

                    MSG_TYPE_MAP.put(msgCode.getNumber(), (GeneratedMessageV3) returnObj);
                    CMD_TYPE_MAP.put(declaredClass, msgCode.getNumber());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static Message.Builder getMsgBuilderByType(int type) {
        if (type < 0) {
            return null;
        }

        Message message = MSG_TYPE_MAP.get(type);
        if (message == null) {
            return null;
        }

        return message.newBuilderForType();
    }


    public static Integer getMsgType(Object msg) {
        if (msg == null) {
            return -1;
        }

        Integer type = CMD_TYPE_MAP.get(msg.getClass());

        if (type == null) {
            return -1;
        }

        return type;
    }
}
