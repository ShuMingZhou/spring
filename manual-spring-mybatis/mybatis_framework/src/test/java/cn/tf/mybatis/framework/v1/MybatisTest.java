package cn.tf.mybatis.framework.v1;

import cn.tf.mybatis.demo.mapper.BlogMapper;
import cn.tf.mybatis.framework.v1.TFConfiguration;
import cn.tf.mybatis.framework.v1.TFExecutor;
import cn.tf.mybatis.framework.v1.TFSqlSession;

public class MybatisTest {

    public static void main(String[] args) {
        TFSqlSession  sqlSession = new TFSqlSession(new TFConfiguration(),new TFExecutor());
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        mapper.selectBlogById(1);
    }

}
