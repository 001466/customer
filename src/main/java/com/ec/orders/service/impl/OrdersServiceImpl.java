package com.ec.orders.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ec.orders.dao.OrdersMapper;
import com.ec.orders.model.Orders;
import com.ec.orders.model.OrdersExample;
import com.ec.orders.service.OrdersService;

@Service
public class OrdersServiceImpl implements OrdersService {

	@Autowired
	private OrdersMapper ordersMapper;

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
		}
	}

	@Override
	public int update(Orders record) {

		OrdersExample example = new OrdersExample();
		example.createCriteria().andIdEqualTo(record.getId());

		try {
			return ordersMapper.updateByExample(record, example);
		} catch (Exception e) {
			if (e instanceof java.sql.SQLSyntaxErrorException
					|| e.getCause() instanceof java.sql.SQLSyntaxErrorException) {
				ordersMapper.createTable();
				return ordersMapper.updateByExample(record, example);
			}
			throw e;
		}

	}

	@Override
	public List<Orders> select(OrdersExample example) {

		try {
			return ordersMapper.selectByExample(example);
		} catch (Exception e) {
			if (e instanceof java.sql.SQLSyntaxErrorException
					|| e.getCause() instanceof java.sql.SQLSyntaxErrorException) {
				ordersMapper.createTable();
				return ordersMapper.selectByExample(example);
			}
			throw e;
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
		}
	
	}

}
