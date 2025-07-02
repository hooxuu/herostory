package cc.xuhao.herostory.entity;

import lombok.Data;

/**
 * @Description:
 *             // 起始位置 X
 *             float fromPosX = 1;
 *             // 起始位置 Y
 *             float fromPosY = 2;
 *             // 移动到位置 X
 *             float toPosX = 3;
 *             // 移动到位置 Y
 *             float toPosY = 4;
 *             // 启程时间戳
 *             uint64 startTime = 5;
 * @Author: xuhao
 * @Date: 2025/6/30 09:49
 */
@Data
public class MoveState {
    
    public float fromPosX;
    public float fromPosY;
    public float toPosX;
    public float toPosY;
    public long startTime;
}
