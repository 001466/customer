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
	
	private void mail(String content) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from); // 发送者
		message.setTo(to.split(",")); // 接受者
		message.setSubject(df.format(new Date())+" Order Notify"); // 发送标题
		message.setText(content); // 发送内容
		javaMailSender.send(message);
	}
	
	
	
	private String parseMailContent(Orders o){
		StringBuilder sb=new StringBuilder();
		sb
		.append("订单编号:").append(o.getId()).append("\r\n")
		.append("订单类型:").append(o.getType()).append("\r\n")
		
		.append("产品生肖:").append(o.getProductBranch()).append("\r\n")
		.append("产品材质:").append(o.getProductMaterial()).append("\r\n")
		
		.append("客户名称:").append(o.getCustomName()).append("\r\n")
		.append("客户手机:").append(o.getCustomMobile()).append("\r\n")
		
		.append("邮寄省份:").append(o.getDeliverProvince()).append("\r\n")
		.append("邮寄城市:").append(o.getDeliverCity()).append("\r\n")
		.append("邮寄地区:").append(o.getDeliverCounty()).append("\r\n")
		.append("详细地址:").append(o.getDeliverAdderss()).append("\r\n")
		.append("方便时间:").append(o.getDeliverTime()).append("\r\n")
		.append("客户留言:").append(o.getCustomContent()).append("\r\n")
		
		
		.append("下单网址:").append(o.getCustomVisitUrl()).append("\r\n")
		.append("订单来源:").append(o.getCustomFrom()).append("\r\n")
		
		.append("操作时间:").append(df.format(o.getCreatetime())).append("\r\n")
		.append("操作网关:").append(o.getCreateip()).append("\r\n")
		.append("操作工具:").append(o.getBrowserOs()).append(",").append(o.getBrowserName()) .append("\r\n")
		.append("入库速率:").append(o.getRateUnit()).append(",").append(o.getRateVal()).append("\r\n")
		
		
		
		.append("\r\n\r\n\r\n");
		return sb.toString();
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
			
			mail(parseMailContent(record));
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
				sb.append(parseMailContent(o)).append("\r\n");
			}
			mail(sb.toString());
		}

	}

}
