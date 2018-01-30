package com.ec.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Lazy(false)
public class ApplicationContext implements ApplicationContextAware {
	private static org.springframework.context.ApplicationContext APPLICATION_CONTEXT;

	@Override
	public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext)
			throws BeansException {
    	System.out.println("User dir="+System.getProperty("user.dir"));  
		APPLICATION_CONTEXT = applicationContext;
	}
	
	public static org.springframework.context.ApplicationContext getAPPLICATION_CONTEXT() {
		return APPLICATION_CONTEXT;
	}

	public static <T> T getBean(String beanName, Class<T> requiredType) throws BeansException{
		return APPLICATION_CONTEXT.getBean(beanName, requiredType);
	}
	
}