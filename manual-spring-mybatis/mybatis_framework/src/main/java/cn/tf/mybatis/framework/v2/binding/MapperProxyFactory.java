package cn.tf.mybatis.framework.v2.binding;

import cn.tf.mybatis.framework.v2.session.DefaultSqlSession;

import java.lang.reflect.Proxy;

public class MapperProxyFactory<T> {

    private Class<T> mapperInterface;
    private Class clazz;

    public MapperProxyFactory(Class<T> mapperInterface, Class clazz) {
        this.mapperInterface = mapperInterface;
        this.clazz = clazz;
    }
    public T newInstance(DefaultSqlSession sqlSession) {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, new MapperProxy(sqlSession, clazz));
    }

}
