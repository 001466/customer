package com.ec.customer.service.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.ec.customer.dao.OrdersMapper;
import com.ec.customer.model.Orders;
import com.ec.customer.service.OrdersService;

@Service
public class OrdersServiceImpl implements OrdersService {

	@Autowired
	private OrdersMapper ordersMapper;

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
			
			mail(to,"Order",record.getCustomName()+","+record.getCustomMobile()+","+record.getProductMaterial()+","+record.getProductBranch());
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
				sb.append(o.getCustomName()).append(",").append(o.getCustomMobile()).append(",").append(o.getProductMaterial()).append(",").append(o.getProductBranch()).append("\r\n");
			}
			mail(to,"Orders",sb.toString());
		}

	}

}
