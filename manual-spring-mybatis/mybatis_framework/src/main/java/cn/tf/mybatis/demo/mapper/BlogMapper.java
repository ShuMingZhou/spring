package cn.tf.mybatis.demo.mapper;



import cn.tf.mybatis.demo.entity.Blog;
import cn.tf.mybatis.framework.v2.annotation.Select;


public interface BlogMapper {
    /**
     * 根据主键查询文章
     * @param bid
     * @return
     */
    @Select("select * from blog where bid = ?")
    public Blog selectBlogById(Integer bid);



}
