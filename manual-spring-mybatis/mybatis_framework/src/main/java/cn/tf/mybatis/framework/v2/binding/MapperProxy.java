package cn.tf.mybatis.framework.v2.binding;


import cn.tf.mybatis.framework.v2.session.DefaultSqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * MapperProxy代理类，用于代理Mapper接口
 */
public class MapperProxy implements InvocationHandler {


    private DefaultSqlSession sqlSession;
    private Class clazz;

    public MapperProxy(DefaultSqlSession sqlSession, Class clazz) {
        this.sqlSession = sqlSession;
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String mapperInterface = method.getDeclaringClass().getName();
        String methodName = method.getName();
        String statementId = mapperInterface+"."+methodName;
        // 如果根据接口类型+方法名能找到映射的SQL，则执行SQL
        if(sqlSession.getConfiguration().hasStatement(statementId)){
            return sqlSession.selectOne(statementId,args,clazz);
        }
        return method.invoke(proxy,args);
    }
}
