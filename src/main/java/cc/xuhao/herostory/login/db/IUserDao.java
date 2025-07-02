package cc.xuhao.herostory.login.db;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/30 21:36
 */
public interface IUserDao {

    UserDO getUserByName(String userName);

    void insertInto(UserDO userDO);
}
