package com.ec.customer;

import com.ec.common.model.Response;

public class Test {

	public static void main(String[] args) {
		System.out.println(new Response<String>(Response.Code.SUCCESS.getValue()).toString());
	}

}
