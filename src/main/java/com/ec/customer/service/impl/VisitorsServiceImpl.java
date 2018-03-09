package com.ec.customer.service.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ec.customer.dao.VisitorsMapper;
import com.ec.customer.model.Visitors;
import com.ec.customer.service.VisitorsService;
@Service
public class VisitorsServiceImpl implements VisitorsService {

	@Autowired
	private VisitorsMapper visitorsMapper;

	@Override
	public int insert(Visitors record) {
		try {
			return visitorsMapper.insert(record);
		} catch (Exception e) {
			if (e instanceof java.sql.SQLSyntaxErrorException
					|| e.getCause() instanceof java.sql.SQLSyntaxErrorException) {
				visitorsMapper.createTable();
				return visitorsMapper.insert(record);
			}
			throw e;
		}
	}



	@Override
	public int insert(Collection<Visitors> record) {

		try {
			return visitorsMapper.insertByList(record);
		} catch (Exception e) {
			if (e instanceof java.sql.SQLSyntaxErrorException
					|| e.getCause() instanceof java.sql.SQLSyntaxErrorException) {
				visitorsMapper.createTable();
				return visitorsMapper.insertByList(record);
			}
			throw e;
		}
	
	}

}
