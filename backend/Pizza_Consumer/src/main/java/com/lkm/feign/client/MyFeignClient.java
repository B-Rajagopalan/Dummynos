package com.lkm.feign.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lkm.dto.bean.PizzaOrderDTOBean;

@FeignClient(name="pizzaproducer",decode404=true)
public interface MyFeignClient 
{
	@RequestMapping(value="pizza/controller/addPizza" , method=RequestMethod.PUT,
			consumes=MediaType.APPLICATION_JSON_VALUE , 
			produces=MediaType.TEXT_HTML_VALUE)
	ResponseEntity<String> addPizza(@RequestBody PizzaOrderDTOBean pizzaBean);
	
	
	@GetMapping(value="pizza/controller/getPizzaDetails", produces=MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<PizzaOrderDTOBean>> getPizzaDetails();
	
	
	@RequestMapping(value="pizza/controller/getDetailsByPizzaName/{pizzaName}" , method=RequestMethod.POST,
			produces=MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<PizzaOrderDTOBean>> getallDetailsByPizzaName(@PathVariable("pizzaName") String pizzaName);
	
	
	@PostMapping(value="pizza/controller/getDetailsByContactNumber/{contactNumber}" ,
			produces=MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<PizzaOrderDTOBean>> getOrderDetailsByContactNumber(@PathVariable("contactNumber") String contactNumber);
}
