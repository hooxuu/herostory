package cc.xuhao.herostory;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;

/**
 * 应该继承接口，简化了
 * @Description:
 * @Author: xuhao
 * @Date: 2025/6/30 21:58
 */
@Slf4j
public final class DBSessionFactory {

    private static SqlSessionFactory sqlSessionFactory;

    private DBSessionFactory() {}


    public static void init() {
        try {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(
                    Resources.getResourceAsStream("MybatisConfig.xml")
            );
        } catch (IOException e) {
            log.error("init mybatis error", e);
        }
    }

    public static SqlSession getSession() {
        if (sqlSessionFactory == null) {
            throw new IllegalStateException("mybatis not initialized");
        }
        return sqlSessionFactory.openSession(true);
    }
}
