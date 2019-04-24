package com.spring.mvcframework.aop.config;

import lombok.Data;

/**
 * Created By Rick 2019/4/22
 */
@Data
public class MAopConfig {
    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectClass;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;
}
