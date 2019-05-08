package cn.tf.interceptor;

import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;

@Intercepts(@Signature(type= Executor.class,method = "query",
    args = {MappedStatement.class,Object.class,RowBounds.class,ResultHandler.class}
))
public class MyPageInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        System.out.println("将逻辑分页改为物理分页,开始！");
        Object[] args = invocation.getArgs();
        // MappedStatement
        MappedStatement ms = (MappedStatement) args[0];
        // Object parameter
        BoundSql bs = ms.getBoundSql(args[1]);
        // RowBounds
        RowBounds rb = (RowBounds) args[2];
        // RowBounds为空时，无需分页
        if(rb==RowBounds.DEFAULT){
            return invocation.proceed();
        }
        //开始分页，在SQL后加上limit语句
        StringBuffer sb = new StringBuffer();
        sb.append(bs.getSql());
        String limit = String.format("limit %d,%d",rb.getOffset(),rb.getLimit());
        sb.append(" ");
        sb.append(limit);
        SqlSource sqlSource = new StaticSqlSource(ms.getConfiguration(),sb.toString(),bs.getParameterMappings());

        Field field = MappedStatement.class.getDeclaredField("sqlSource");
        field.setAccessible(true);
        field.set(ms,sqlSource);
        Object proceed = invocation.proceed();

        System.out.println("将逻辑分页改为物理分页,结束！");
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
