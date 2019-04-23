package com.spring.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created By Rick 2019/4/23
 */
@Data
@AllArgsConstructor
public class User {
    private String id;

    private String name;

    private int age;
}
