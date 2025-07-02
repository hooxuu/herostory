package cc.xuhao.herostory.login.db;

import lombok.Data;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/30 21:32
 */
@Data
public class UserDO {

    public Integer userId;

    public String userName;

    public String password;

    public String heroAvatar;
}

