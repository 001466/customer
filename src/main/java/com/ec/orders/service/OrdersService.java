package com.ec.orders.service;

import java.util.List;

import com.ec.orders.model.Orders;
import com.ec.orders.model.OrdersExample;

public interface OrdersService {
	public int insert(Orders record);

	public int update(Orders record);

	public List<Orders> select(OrdersExample example);
}
