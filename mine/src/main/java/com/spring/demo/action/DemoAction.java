package com.spring.demo.action;



import com.spring.mvcframework.annotation.MController;
import com.spring.mvcframework.annotation.MRequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//虽然，用法一样，但是没有功能
@MController
@MRequestMapping("/demo")
public class DemoAction {

	@MRequestMapping("/query")
	public void query(HttpServletRequest req, HttpServletResponse resp){
		System.out.println("startup");
	}


}
