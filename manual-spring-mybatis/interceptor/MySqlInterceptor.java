package cn.tf.interceptor;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;

import java.sql.Statement;
import java.util.Properties;

@Intercepts(@Signature(type= StatementHandler.class,method = "query",
        args = {Statement.class, ResultHandler.class}))
public class MySqlInterceptor implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql bs = statementHandler.getBoundSql();
        String sql = bs.getSql();
        System.out.println("获取到的sql语句是:"+sql);
        Object proceed = invocation.proceed();

        long endTime = System.currentTimeMillis();
        System.out.println("Sql执行时间为:"+(endTime - startTime)+"ms！");

        return proceed;
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o,this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
