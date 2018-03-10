package com.ec.customer.service.impl;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import com.ec.customer.dao.OrdersMapper;
import com.ec.customer.model.Orders;
import com.ec.customer.service.OrdersService;

@Service
@EnableScheduling
public class OrdersServiceImpl implements OrdersService {

	@Autowired
	private OrdersMapper ordersMapper;

	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String from;

	@Value("${security.mail.to}")
	private String to;
	
	java.text.SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public void mail(String content) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from); // 发送者
		message.setTo(to); // 接受者
		message.setSubject(df.format(new Date())+" Order Notify"); // 发送标题
		message.setText(content); // 发送内容
		javaMailSender.send(message);
	}

	@Override
	public int insert(Orders record) {
		try {
			
			return ordersMapper.insert(record);
			
		} catch (Exception e) {
			if (e instanceof java.sql.SQLSyntaxErrorException
					|| e.getCause() instanceof java.sql.SQLSyntaxErrorException) {
				ordersMapper.createTable();
				return ordersMapper.insert(record);
			}
			
			throw e;
		}finally{
			
			mail(record.getCustomName()+" \t "+record.getCustomMobile()+" \t "+record.getProductMaterial()+" \t "+record.getProductBranch());
		}
	}

	@Override
	public int insert(Collection<Orders> record) {
		

		try {
			return ordersMapper.insertByList(record);
		} catch (Exception e) {
			if (e instanceof java.sql.SQLSyntaxErrorException
					|| e.getCause() instanceof java.sql.SQLSyntaxErrorException) {
				ordersMapper.createTable();
				return ordersMapper.insertByList(record);
			}
			throw e;
		}finally{
			StringBuilder sb=new StringBuilder();
			for(Orders o:record){
				sb.append(o.getCustomName()).append(" \t ").append(o.getCustomMobile()).append(" \t ").append(o.getProductMaterial()).append(" \t ").append(o.getProductBranch()).append("\r\n");
			}
			mail(sb.toString());
		}

	}

}
