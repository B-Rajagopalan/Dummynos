package com.lkm.feign.client;

import org.springframework.http.HttpStatus;

import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder
{

	@Override
	public Exception decode(String methodKey, Response response) {
		if(response.status()== HttpStatus.INTERNAL_SERVER_ERROR.value() || 
				response.status()==HttpStatus.NOT_FOUND.value())
			return new RuntimeException("Issue with the inputs or data may not be present");
		
		return null;
	}
	
}
