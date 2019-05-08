package cn.tf.mybatis.framework.v2.interceptor;

import cn.tf.mybatis.framework.v2.annotation.Intercepts;
import cn.tf.mybatis.framework.v2.plugin.Interceptor;
import cn.tf.mybatis.framework.v2.plugin.Invocation;
import cn.tf.mybatis.framework.v2.plugin.Plugin;
import java.util.Arrays;

@Intercepts("query")
public class MySqlInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();
        String sql = (String) invocation.getArgs()[0];
        Object[] parameter = (Object[]) invocation.getArgs()[1];
        System.out.println("获取到的sql语句是:"+sql);
        System.out.println("获取到的参数是:"+ Arrays.toString(parameter));
        Object proceed = invocation.proceed();

        long endTime = System.currentTimeMillis();
        System.out.println("Sql执行时间为:"+(endTime - startTime)+"ms！");

        return proceed;
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o,this);
    }

}
