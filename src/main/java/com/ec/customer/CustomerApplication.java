package com.ec.customer;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.ec.common.util.IDGen;
@SpringBootApplication
@ComponentScan(basePackages={"com.ec"})
@MapperScan("com.ec.customer.dao")
@EnableScheduling
public class CustomerApplication {
	
	private   static final Logger   LOGGER = LoggerFactory.getLogger(CustomerApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(CustomerApplication.class, args);
	}
	
	@Scheduled(cron = "${cron.reset-id:0 1 0 * * ?}")
	private void scheduler() {
		LOGGER.info("RESET ID:"+IDGen.reset());
	}
	
}
