package com.lkm.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lkm.DTO.bean.PizzaOrderDTOBean;
import com.lkm.service.PizzaService;


@RestController
public class PizzaController 
{
	@Autowired
	PizzaService pizzaService;
	
	@RequestMapping(value="pizza/controller/addPizza" , method=RequestMethod.PUT,
			consumes=MediaType.APPLICATION_JSON_VALUE , 
			produces=MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<String> addPizza(@Valid @RequestBody PizzaOrderDTOBean pizzaBean, BindingResult result)
	{
		if(result.hasErrors()) {
			return new ResponseEntity<String>(result.getAllErrors().toString(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		int idNumber = pizzaService.addPizza(pizzaBean);
		return new ResponseEntity<String>("Pizza added successfully with id number "+idNumber,HttpStatus.OK);
	}
	
	@GetMapping(value="pizza/controller/getPizzaDetails", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PizzaOrderDTOBean>> getPizzaDetails() throws InterruptedException
	{
		List<PizzaOrderDTOBean> pizzaList = pizzaService.getPizzaDetails();
		System.out.println("Primary resource");
		return new ResponseEntity<List<PizzaOrderDTOBean>>(pizzaList,HttpStatus.OK);
	}
	
	@RequestMapping(value="pizza/controller/getDetailsByPizzaName/{pizzaName}" , method=RequestMethod.POST,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PizzaOrderDTOBean>> getallDetailsByPizzaName(@PathVariable("pizzaName") String pizzaName)
	{
		List<PizzaOrderDTOBean> pizzaBeanList = pizzaService.getallDetailsByPizzaName(pizzaName);
		if(pizzaBeanList.isEmpty()) {
			return new ResponseEntity<List<PizzaOrderDTOBean>>(pizzaBeanList,HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<PizzaOrderDTOBean>>(pizzaBeanList,HttpStatus.OK);
	}
	
	@RequestMapping(value="pizza/controller/getDetailsByContactNumber/{contactNumber}" , method=RequestMethod.POST,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PizzaOrderDTOBean>> getOrderDetailsByContactNumber(@PathVariable("contactNumber") String contactNumber)
	{
		List<PizzaOrderDTOBean> pizzaBeanList = pizzaService.getOrderDetailsByContactNumber(contactNumber);
		if(pizzaBeanList.isEmpty()) {
			return new ResponseEntity<List<PizzaOrderDTOBean>>(pizzaBeanList,HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<PizzaOrderDTOBean>>(pizzaBeanList,HttpStatus.OK);
	}
}
