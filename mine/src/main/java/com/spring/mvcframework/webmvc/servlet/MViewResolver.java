package com.spring.mvcframework.webmvc.servlet;

import java.io.File;
import java.util.Locale;

/**
 * Created By Rick 2019/4/19
 */
public class MViewResolver {
    private final String DEFAULT_TEMPLATE_SUFFX = ".html";
    private File templateRootDir;

    public MViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templateRootPath);
    }

    public MView resolveViewName(String viewName, Locale locale) throws Exception{
        if(null == viewName || "".equals(viewName.trim())){return null;}
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+","/"));
        return new MView(templateFile);
    }
}
