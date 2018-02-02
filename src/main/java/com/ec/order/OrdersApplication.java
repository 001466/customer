package com.ec.order;

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
@MapperScan("com.ec.order.dao")
@EnableScheduling
public class OrdersApplication {
	
	private   static final Logger   LOGGER = LoggerFactory.getLogger(OrdersApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(OrdersApplication.class, args);
	}
	
	@Scheduled(cron = "${cron.reset-id:0 1 0 * * ?}")
	private void scheduler() {
		LOGGER.info("RESET ID:"+IDGen.reset());
	}
	
}
