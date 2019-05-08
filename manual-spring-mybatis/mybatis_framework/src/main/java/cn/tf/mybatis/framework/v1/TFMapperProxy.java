package cn.tf.mybatis.framework.v1;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class TFMapperProxy  implements InvocationHandler {
    private TFSqlSession sqlSession;

    public TFMapperProxy(TFSqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String statementId = method.getDeclaringClass().getName()+"."+method.getName();


        return sqlSession.selectOne(statementId,args[0]);
    }
}
