package com.ec.common.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author winall
 *
 */
public class BaseEntity {
	
	private   static final Logger   LOGGER = LoggerFactory.getLogger(BaseEntity.class);

	@JsonIgnore
	public BaseEntity() {
		super();
	}

	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		String json = null;
		try {
			json = mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			LOGGER.warn(e.getMessage(),e);
		}
		return json;
	}

}
