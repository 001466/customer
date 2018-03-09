package com.ec.customer.service;

import java.util.Collection;

import com.ec.customer.model.Visitors;

public interface VisitorsService {

	
	public int insert(Visitors record);
	
	public int insert(Collection<Visitors> record);


}
