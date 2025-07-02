package cc.xuhao.herostory.entity;

import lombok.Data;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/25 14:08
 */

@Data
public class User {

    public Integer id;

    public String userName;

    public String heroAvatar;

    // 初始血量100
    public int hp = 100;

    public MoveState moveState = new MoveState();
}
