package com.ec.orders.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author winall
 *
 */
@RestController
@RequestMapping(value = "/test")
public class TestController  {

	private static final Logger log = LoggerFactory.getLogger(TestController.class);

	@RequestMapping()
	public void  test(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.err.println(request.getRequestURL());
		response.sendRedirect("https://www.baidu.com");
	}

 

}
