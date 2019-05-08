package cn.tf.mybatis.framework.v2.executor;


import cn.tf.mybatis.framework.v2.cache.CacheKey;
import cn.tf.mybatis.framework.v2.utils.ToolUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 带缓存的执行器，用于装饰基本执行器
 */
public class CachingExecutor implements Executor {

    private Executor delegate;
    private static final Map<Integer, Object> cache = new HashMap<>();

    public CachingExecutor(Executor delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> T query(String statement, Object[] parameter, Class pojo)  {
        // 计算CacheKey
        CacheKey cacheKey = new CacheKey();
        cacheKey.update(statement);
        // 为了命中缓存，把Object[]转换成逗号拼接的字符串，因为对象的HashCode都不一样
        cacheKey.update(ToolUtils.joinStr(parameter));
        // 是否拿到缓存
        if (cache.containsKey(cacheKey.getCode())) {
            // 命中缓存
            System.out.println("【命中缓存】");
            return (T)cache.get(cacheKey.getCode());
        }else{
            // 没有的话调用被装饰的SimpleExecutor从数据库查询
            Object obj = delegate.query(statement, parameter, pojo);
            cache.put(cacheKey.getCode(), obj);
            return (T)obj;
        }
    }



}
