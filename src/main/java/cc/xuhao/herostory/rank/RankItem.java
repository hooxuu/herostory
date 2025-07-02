package cc.xuhao.herostory.rank;

import lombok.Data;

/**
 * @Description: 排名榜的一条数据
 * @Author: xuhao
 * @Date: 2025/7/2 15:31
 */
@Data
public class RankItem {
    /**
     * 排名 Id
     */
    public int rankId;

    /**
     * 用户 Id
     */
    public int userId;

    /**
     * 用户名称
     */
    public String userName;

    /**
     * 英雄形象
     */
    public String heroAvatar;

    /**
     * 胜利次数
     */
    public int win;
}
