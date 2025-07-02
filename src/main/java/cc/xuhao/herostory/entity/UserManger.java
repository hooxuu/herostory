package cc.xuhao.herostory.entity;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/26 09:31
 */
public final class UserManger {

    public static final String USER_ID = "userId";

    private static final Map<Integer, User> USER_MAP = new ConcurrentHashMap<>(128);

    private UserManger() {}

    public static User getUser(Integer userId) {
        return USER_MAP.get(userId);
    }

    public static void addUser(User user) {
        USER_MAP.put(user.id, user);
    }

    public static User removeUser(Integer userId) {
        return USER_MAP.remove(userId);
    }

    public static Collection<User> listAllUser() {
        return USER_MAP.values();
    }

    public static User getUserByUserId(Integer userId) {
        return USER_MAP.get(userId);
    }
}
