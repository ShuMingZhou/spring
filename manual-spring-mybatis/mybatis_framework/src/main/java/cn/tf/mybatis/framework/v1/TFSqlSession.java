package cn.tf.mybatis.framework.v1;

public class TFSqlSession {
    private TFConfiguration configuration; //配置类
    private TFExecutor executor;  //执行器

    public TFSqlSession(TFConfiguration configuration, TFExecutor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    /**
     * 执行单条查询
     * @param statementId
     * @param parameter
     * @param <T>
     * @return
     */
    public <T> T selectOne(String statementId,Object parameter){
        String sql = TFConfiguration.sqlMapping.getString(statementId);
        return executor.query(sql,parameter);
    }

    /**
     * 获取一个代理对象
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getMapper(Class clazz){
       return  configuration.getMapper(clazz,this);
    }


}
