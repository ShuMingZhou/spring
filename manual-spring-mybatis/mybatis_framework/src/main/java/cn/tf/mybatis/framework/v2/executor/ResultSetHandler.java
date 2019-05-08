package cn.tf.mybatis.framework.v2.executor;

import cn.tf.mybatis.framework.v2.utils.ToolUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 结果集处理器
 */
public class ResultSetHandler {

    public <T> T handle(ResultSet resultSet,Class clazz){
        Object pojo = null;

        try {
            pojo = clazz.newInstance();
            // 遍历结果集
            if (resultSet.next()) {
                for (Field field : pojo.getClass().getDeclaredFields()) {
                    setValue(pojo, field, resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (T)pojo;


    }

    /**
     * 通过反射给属性赋值
     */
    private void setValue(Object pojo, Field field, ResultSet rs)  {
        try{
            Method setMethod = pojo.getClass().getMethod("set" + ToolUtils.lowerFirstCase(field.getName()), field.getType());
            setMethod.invoke(pojo, getResult(rs, field));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 根据反射判断类型，从ResultSet中取对应类型参数
     */
    private Object getResult(ResultSet rs, Field field) throws SQLException {
        Class type = field.getType();
        String dataName = ToolUtils.HumpToUnderline(field.getName()); // 驼峰转下划线
        if (Integer.class == type ) {
            return rs.getInt(dataName);
        }else if (String.class == type) {
            return rs.getString(dataName);
        }else if(Long.class == type){
            return rs.getLong(dataName);
        }else if(Boolean.class == type){
            return rs.getBoolean(dataName);
        }else if(Double.class == type){
            return rs.getDouble(dataName);
        }else{
            return rs.getString(dataName);
        }
    }



}
