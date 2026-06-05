package com.lkm.test.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils 
{
	public static <T> T convertJsonToObject(String json, Class<T> var) throws Exception
	{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, var);
	}
	
	public static String convertObjectToJson(Object obj) throws JsonProcessingException
	{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}
}
