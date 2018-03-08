package com.ec.customer.service;

import java.util.Collection;
import java.util.List;

import com.ec.customer.model.Orders;
import com.ec.customer.model.OrdersExample;

public interface OrdersService {
	public int insert(Orders record);
	
	public int insert(Collection<Orders> record);

	public int update(Orders record);

	public List<Orders> select(OrdersExample example);
}
