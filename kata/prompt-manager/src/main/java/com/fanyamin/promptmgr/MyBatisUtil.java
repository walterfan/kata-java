package com.fanyamin.promptmgr;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class MyBatisUtil {

    // Static SqlSessionFactory instance
    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            // Load MyBatis configuration file
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Error initializing MyBatis SqlSessionFactory", e);
        }
    }

    /**
     * Get a new SqlSession from the SqlSessionFactory.
     * By default, auto-commit is disabled.
     *
     * @return SqlSession
     */
    public static SqlSession getSqlSession() {
        return sqlSessionFactory.openSession();
    }

    /**
     * Get the SqlSessionFactory instance.
     *
     * @return SqlSessionFactory
     */
    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}
