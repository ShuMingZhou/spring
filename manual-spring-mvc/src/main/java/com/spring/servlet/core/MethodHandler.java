package com.spring.servlet.core;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created By Rick 2019/4/23
 */
@Data
public class MethodHandler {
    //方法所在的类
    private Object object;

    private Method method;
    //参数顺序
    private List<String> params;
}
