package cn.tf.mybatis.framework.v1;

import java.lang.reflect.Proxy;
import java.util.ResourceBundle;

public class TFConfiguration {

    public final static ResourceBundle sqlMapping;

    static{
        sqlMapping = ResourceBundle.getBundle("v1sql");
    }

    /**
     * 返回接口的代理类对象
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getMapper(Class clazz,TFSqlSession sqlSession) {
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[]{clazz},new TFMapperProxy(sqlSession));
    }
}
