package cn.tf.mybatis.framework.v2;

import cn.tf.mybatis.demo.entity.Blog;
import cn.tf.mybatis.demo.mapper.BlogMapper;
import cn.tf.mybatis.framework.v2.session.DefaultSqlSession;
import cn.tf.mybatis.framework.v2.session.SqlSessionFactory;

public class MybatisTestV2 {

    public static void main(String[] args) {
        SqlSessionFactory factory = new SqlSessionFactory();
        DefaultSqlSession sqlSession = factory.build().openSqlSession();
        // 获取MapperProxy代理
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        Blog blog = mapper.selectBlogById(1);

        System.out.println("第一次查询: " + blog);
        System.out.println();
        blog = mapper.selectBlogById(1);
        System.out.println("第二次查询: " + blog);
    }
}
