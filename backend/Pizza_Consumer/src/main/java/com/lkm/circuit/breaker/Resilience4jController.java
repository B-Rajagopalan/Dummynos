package com.lkm.circuit.breaker;

import java.util.List;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


import com.lkm.dto.bean.PizzaOrderDTOBean;
import com.lkm.feign.client.MyFeignClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@Component
public class  Resilience4jController
{
	private static final String RESILIENCE4J_CIRCUIT_BREAKER = "myCircuitBreaker";
	private static final String RESILIENCE4J_RATE_LIMITER = "myRateLimiter";
	
	@Autowired
	private MyFeignClient myFeignClient;

	@CircuitBreaker(name = RESILIENCE4J_CIRCUIT_BREAKER, fallbackMethod = "circuitBreakerCustomFallBack")
	public ResponseEntity<String> addPizza(PizzaOrderDTOBean pizzaBean)
	{
		return myFeignClient.addPizza(pizzaBean);
	}
	
	@RateLimiter(name = RESILIENCE4J_RATE_LIMITER, fallbackMethod = "rateLimiterCustomFallBack")
	@CircuitBreaker(name = RESILIENCE4J_CIRCUIT_BREAKER, fallbackMethod = "circuitBreakerCustomFallBack")
	public ResponseEntity<List<PizzaOrderDTOBean>> getPizzaDetails()
	{
		return myFeignClient.getPizzaDetails();
		
	}
	
	@CircuitBreaker(name = RESILIENCE4J_CIRCUIT_BREAKER, fallbackMethod = "circuitBreakerCustomFallBack")
	public ResponseEntity<List<PizzaOrderDTOBean>> getallDetailsByPizzaName(String pizzaName)
	{
		return myFeignClient.getallDetailsByPizzaName(pizzaName);
		
	}
	
	@CircuitBreaker(name = RESILIENCE4J_CIRCUIT_BREAKER, fallbackMethod = "circuitBreakerCustomFallBack")
	public ResponseEntity<List<PizzaOrderDTOBean>> getOrderDetailsByContactNumber(String contactNumber)
	{
		return myFeignClient.getOrderDetailsByContactNumber(contactNumber);
		
	}
	
	public ResponseEntity<String> circuitBreakerCustomFallBack(Exception e)
	{
		return new ResponseEntity<String>("Unexpected Error", HttpStatus.INTERNAL_SERVER_ERROR);
		
	}

	public ResponseEntity<String> rateLimiterCustomFallBack(RequestNotPermitted e)
	{
		return new ResponseEntity<String>("Too many requests, try again later", HttpStatus.TOO_MANY_REQUESTS);

	}

}
