package com.spring.mvcframework.webmvc.servlet;

import lombok.Data;

import java.util.Map;

/**
 * Created By Rick 2019/4/19
 */
@Data
public class MModelAndView {
    private String viewName;
    private Map<String,?> model;

    public MModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public MModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }
}
