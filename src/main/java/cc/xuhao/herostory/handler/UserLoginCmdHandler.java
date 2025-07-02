package cc.xuhao.herostory.handler;

import cc.xuhao.herostory.entity.User;
import cc.xuhao.herostory.entity.UserManger;
import cc.xuhao.herostory.login.LoginService;
import cc.xuhao.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/30 21:26
 */
public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd>{
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {
        if (null == ctx || null == cmd) {
            return;
        }

        String userName = cmd.getUserName();
        String password = cmd.getPassword();

        LoginService.instance().login(userName, password, userDO -> {
            User user = new User();
            user.id = userDO.userId;
            user.userName = userDO.userName;
            user.heroAvatar = userDO.heroAvatar;
            UserManger.addUser(user);
            ctx.channel().attr(AttributeKey.valueOf(UserManger.USER_ID)).set(user.id);

            // 登陆结果构建者
            GameMsgProtocol.UserLoginResult.Builder
                    resultBuilder = GameMsgProtocol.UserLoginResult.newBuilder();
            resultBuilder.setUserId(userDO.userId);
            resultBuilder.setUserName(userDO.userName);
            resultBuilder.setHeroAvatar(userDO.heroAvatar);

            // 构建结果并发送
            GameMsgProtocol.UserLoginResult newResult = resultBuilder.build();
            ctx.writeAndFlush(newResult);
            return null;
        });
    }
}
