package com.spring.servlet;

import com.alibaba.fastjson.JSON;
import com.spring.servlet.annotation.*;
import com.spring.servlet.core.MethodHandler;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodParameterNamesScanner;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * Created By Rick 2019/4/23
 */
public class MyDispatcherServlet extends HttpServlet {
    //spring配置文件
    private Properties properties = new Properties();
    //存放所有带注解的类
    private List<String> classNameList = new ArrayList<>();
    //IOC容器,通过类型注入
    private Map<String, Object> IOCByType = new HashMap<>();
    //当通过类型找不到对应实例时，通过名称注入(名称相同时会覆盖之前的值，这里就不处理了)
    private Map<String, Object> IOCByName = new HashMap<>();
    //url 到controller方法的映射
    private Map<String, MethodHandler> urlHandler = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //6、处理请求，执行相应的方法
        doHandler(req, resp);
    }

    @Override
    public void init() throws ServletException {

        System.out.println("servlet开始初始化");
        //1、定位配置文件 spring-config.properties,获取扫描路径
        doLoadConfig();
        //2、加载扫描配置的路径下的带有注解的类
        doScanner(properties.getProperty("basepackage"));
        //3、注册，初始化所有的类，被放入到伪IOC容器中
        doPutIoc();
        //4、实现@MAutowried自动注入
        doAutowried();
        //5、初始化HandlerMapping，根据url映射不同的controller方法
        doMapping();
        System.out.println("servlet初始化完成");
    }

    //1、加载配置文件 spring-config.properties,获取扫描路径
    private void doLoadConfig() {
        //ServletConfig:代表当前Servlet在web.xml中的配置信息
        ServletConfig config = this.getServletConfig();
        String configLocation = config.getInitParameter("contextConfigLocation");
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(configLocation.replace("classpath:",""));
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //2、扫描配置的路径下的带有注解的类
    private void doScanner(String path) {
        //java文件
        if (path.endsWith(".class")) {
            //获取到带有包路径的类名
            String className = path.substring(0, path.lastIndexOf(".class"));
            //扫描的类
            classNameList.add(className);
            return;
        }
        URL url = this.getClass().getClassLoader().getResource("/" + path.replaceAll("\\.", "/"));
        //是包路径，继续迭代
        File file = new File(url.getFile());
        File[] files = file.listFiles();
        for (File f : files) {
            doScanner(path + "." + f.getName());
        }
    }

    //3、初始化所有的类，被放入到伪IOC容器中
    private void doPutIoc() {
        if (classNameList.isEmpty()) {
            return;
        }
        try {
            for (String className : classNameList) {
                //反射获取实例对象
                Class<?> clazz = Class.forName(className);
                //IOC容器key命名规则：1.默认类名首字母小写  2.使用用户自定义名，如 @MService("abc") 3.如果service实现了接口，可以使用接口作为key

                //controller,service注解类
                if (clazz.isAnnotationPresent(MController.class)) {
                    MController mController = clazz.getAnnotation(MController.class);
                    String beanName = mController.value().trim();
                    //如果用户没有定义名称，使用名首字母小写
                    if (StringUtils.isBlank(beanName)) {
                        beanName = lowerFirstCase(clazz.getSimpleName());
                    }
                    //byName
                    Object instance = clazz.newInstance();
                    IOCByName.put(beanName, instance);
                    //byType
                    IOCByType.put(clazz.getName(), instance);
                } else if (clazz.isAnnotationPresent(MService.class)) {
                    MService uvService = clazz.getAnnotation(MService.class);
                    String beanName = uvService.value().trim();
                    //如果用户没有定义名称，使用名首字母小写
                    if (StringUtils.isBlank(beanName)) {
                        beanName = lowerFirstCase(clazz.getSimpleName());
                    }
                    //byName
                    Object instance = clazz.newInstance();
                    IOCByName.put(beanName, instance);
                    //byType
                    IOCByType.put(clazz.getName(), instance);
                    //如果service实现了接口，可以使用接口作为key
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> interf : interfaces) {
                        IOCByName.put(lowerFirstCase(interf.getSimpleName()), instance);
                        IOCByType.put(interf.getName(), instance);
                    }
                } else {
                    continue;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //4、实现@UVAutowried自动注入
    private void doAutowried() {
        if (IOCByName.isEmpty() && IOCByType.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : IOCByType.entrySet()) {
            //获取变量
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                //private、protected修饰的变量可访问
                field.setAccessible(true);

                if (!field.isAnnotationPresent(MAutowired.class)) {
                    continue;
                }
                Object instance = null;
                String beanName = field.getType().getName();
                String simpleName = lowerFirstCase(field.getType().getSimpleName());
                //首先根据Type注入，没有实例时根据Name，否则抛出异常
                if (IOCByType.containsKey(beanName)) {
                    instance = IOCByType.get(beanName);
                } else if (IOCByName.containsKey(simpleName)) {
                    instance = IOCByName.get(simpleName);
                } else {
                    throw new RuntimeException("not find class to autowire");
                }
                try {
                    //向obj对象的这个Field设置新值value,依赖注入
                    field.set(entry.getValue(), instance);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //5、初始化HandlerMapping，根据url映射不同的controller方法
    private void doMapping() {
        if (IOCByType.isEmpty() && IOCByName.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : IOCByType.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            //判断是否是controller
            if (!clazz.isAnnotationPresent(MController.class)) {
                continue;
            }
            String startUrl = "/";
            //判断controller类上是否有MRequestMapping注解，如果有则拼接url
            if (clazz.isAnnotationPresent(MRequestMapping.class)) {
                MRequestMapping requestMapping = clazz.getAnnotation(MRequestMapping.class);
                String value = requestMapping.value();
                if (!StringUtils.isBlank(value)) {
                    startUrl += value;
                }
            }
            //遍历controller类中MRequestMapping注解修饰的方法，添加到urlHandler中,完成url到方法的映射
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(MRequestMapping.class)) {
                    continue;
                }
                MRequestMapping annotation = method.getAnnotation(MRequestMapping.class);
                String url = startUrl + "/" + annotation.value().trim();
                //解决多个/重叠的问题
                url = url.replaceAll("/+", "/");

                MethodHandler methodHandler = new MethodHandler();
                //放入方法
                methodHandler.setMethod(method);
                try {
                    //放入方法所在的controller
                    methodHandler.setObject(entry.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //放入方法的参数列表
                List<String> params = doParamHandler(method);
                methodHandler.setParams(params);
                urlHandler.put(url, methodHandler);
            }
        }
    }

    //6、处理请求，执行相应的方法
    private void doHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean jsonResult = false;
        String uri = request.getRequestURI();
        System.out.println("uri=="+uri);
        PrintWriter writer = response.getWriter();
        for (Map.Entry<String,MethodHandler> map :urlHandler.entrySet()) {
            String key = map.getKey();
            System.out.println("key=="+key);
        }
        //没有映射的url，返回404
        if (!urlHandler.containsKey(uri)) {
            writer.write("404 Not Found");
            return;
        }
        //获取url对应的method包装类
        MethodHandler methodHandler = urlHandler.get(uri);
        //处理url的method
        Method method = methodHandler.getMethod();
        //method所在的controller
        Object object = methodHandler.getObject();
        //method的参数列表
        List<String> params = methodHandler.getParams();

        //如果controller或这个方法有MResponseBody修饰，返回json
        if (object.getClass().isAnnotationPresent(MResponseBody.class) || method.isAnnotationPresent(MResponseBody.class)) {
            jsonResult = true;
        }
        List<Object> args = new ArrayList<>();
        for (String param : params) {
            //从request中获取参数，然后放入参数列表
            String parameter = request.getParameter(param);
            args.add(parameter);
        }

        try {
            //执行方法，处理，返回结果
            Object result = method.invoke(object, args.toArray());
            //返回json(使用阿里的fastJson)
            if (jsonResult) {
                writer.write(JSON.toJSONString(object));
            } else { //返回视图
                doResolveView((String) result, request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //方法执行异常，返回500
            writer.write("500 Internal Server Error");
            return;
        }

    }

    //7、视图解析，返回视图
    private void doResolveView(String indexView, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //视图前缀
        String prefix = properties.getProperty("view.prefix");
        //视图后缀
        String suffix = properties.getProperty("view.suffix");
        String view = (prefix + indexView + suffix).trim().replaceAll("/+", "/");
        request.getRequestDispatcher(view).forward(request, response);
    }

    //处理字符串首字母小写
    private String lowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        //ascii码计算
        chars[0] += 32;
        return String.valueOf(chars);
    }


    /**
     * 在Java 8之前的版本，代码编译为class文件后，方法参数的类型是固定的，但参数名称却丢失了，
     * 这和动态语言严重依赖参数名称形成了鲜明对比。 现在，Java 8开始在class文件中保留参数名，
     * 给反射带来了极大的便利。 使用reflections包，jdk7和jdk8都可用
     **/
    //处理method的参数
    private List<String> doParamHandler(Method method) {
        //使用reflections进行参数名的获取
        Reflections reflections = new Reflections(new MethodParameterNamesScanner());
        //参数名与顺序对应
        List<String> paramNames = reflections.getMethodParamNames(method);
        return paramNames;
    }

}
