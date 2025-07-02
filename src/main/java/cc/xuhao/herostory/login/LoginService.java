package cc.xuhao.herostory.login;

import cc.xuhao.herostory.DBSessionFactory;
import cc.xuhao.herostory.async.AsyncOperationProcessor;
import cc.xuhao.herostory.async.IAsyncOperation;
import cc.xuhao.herostory.login.db.IUserDao;
import cc.xuhao.herostory.login.db.UserDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.util.function.Function;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/30 21:29
 */
public final class LoginService {

    private static final LoginService INSTANCE = new LoginService();

    private LoginService() {}

    public static LoginService instance() {
        return INSTANCE;
    }

    public void login(String userName, String password, Function<UserDO, Void> callback) {
        if (userName == null || userName.isEmpty()
                || password == null || password.isEmpty()) {
            return;
        }

        AsyncDoLoginOperation ado = new AsyncDoLoginOperation(userName, password) {
            @Override
            public void doFinish() {
                callback.apply(getUser());
            }
        };

        AsyncOperationProcessor.instance().process(ado);
    }

}

@Slf4j
class AsyncDoLoginOperation implements IAsyncOperation {

    private String userName;
    private String password;
    private UserDO _user;

    public AsyncDoLoginOperation(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public UserDO getUser() {
        return _user;
    }

    @Override
    public int bindId() {
        return this.userName.charAt(this.userName.length() - 1);
    }

    @Override
    public void doAsync() {
        try(SqlSession session = DBSessionFactory.getSession()) {
            IUserDao mapper = session.getMapper(IUserDao.class);

            // 看看当前线程
            log.info("当前线程 = {}", Thread.currentThread().getName());

            UserDO user = mapper.getUserByName(userName);

            if (user == null) {
                // 执行新增
                user = new UserDO();
                user.userName = userName;
                user.password = password;
                user.heroAvatar = "Hero_Shaman";
                mapper.insertInto(user);
            } else {
                // 判断用户密码
                if (!password.equals(user.password)) {
                    // 用户密码错误,
                    log.error(
                            "用户密码错误, userId = {}, userName = {}",
                            user.userId,
                            user.userName
                    );

                    return;
                }
            }

            _user = user;
        } catch (Exception e) {
            log.error("do async error", e);
        }
    }
}
