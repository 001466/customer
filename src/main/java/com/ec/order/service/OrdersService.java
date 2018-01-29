package com.ec.order.service;

import java.util.Collection;
import java.util.List;

import com.ec.order.model.Orders;
import com.ec.order.model.OrdersExample;

public interface OrdersService {
	public int insert(Orders record);
	
	public int insert(Collection<Orders> record);

	public int update(Orders record);

	public List<Orders> select(OrdersExample example);
}
