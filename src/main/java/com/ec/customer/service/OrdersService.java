package com.ec.customer.service;

import java.util.Collection;

import com.ec.customer.model.Orders;

public interface OrdersService {
	
	public int insert(Orders record);
	
	public int insert(Collection<Orders> record);

}
