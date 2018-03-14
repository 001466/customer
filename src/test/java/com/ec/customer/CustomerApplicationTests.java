package com.ec.customer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerApplicationTests {

	
	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String from;

	@Value("${security.mail.to}")
	private String to;
	
	public void mail(String to, String title, String content) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from); // 发送者
		message.setTo(to); // 接受者
		message.setSubject(title); // 发送标题
		message.setText(content); // 发送内容
		javaMailSender.send(message);
	}
	
	@Test
	public void contextLoads() {
		//mail(to,"Test","Test");
		
		System.err.println("xxxxx");
	}

}
