package com.lkm.feign.client;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lkm.circuit.breaker.Resilience4jController;
import com.lkm.dto.bean.PizzaOrderDTOBean;

@RestController
public class FeignController 
{
	@Autowired
	Resilience4jController circuitBreaker;
	
	@RequestMapping(value="pizza/controller/addPizza" , method=RequestMethod.PUT,
			consumes=MediaType.APPLICATION_JSON_VALUE , 
			produces=MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<String> addPizza(@RequestBody PizzaOrderDTOBean pizzaBean, BindingResult result)
	{
		return circuitBreaker.addPizza(pizzaBean);
		
	}
	
	@GetMapping(value="pizza/controller/getPizzaDetails", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PizzaOrderDTOBean>> getPizzaDetails()
	{
		return circuitBreaker.getPizzaDetails();
		
	}
	
	@RequestMapping(value="pizza/controller/getDetailsByPizzaName/{pizzaName}" , method=RequestMethod.POST,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PizzaOrderDTOBean>> getallDetailsByPizzaName(@PathVariable("pizzaName") String pizzaName)
	{
		return circuitBreaker.getallDetailsByPizzaName(pizzaName);
		
	}
	
	@PostMapping(value="pizza/controller/getDetailsByContactNumber/{contactNumber}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PizzaOrderDTOBean>> getOrderDetailsByContactNumber(@PathVariable("contactNumber") String contactNumber)
	{
		return circuitBreaker.getOrderDetailsByContactNumber(contactNumber);
		
	}
}
