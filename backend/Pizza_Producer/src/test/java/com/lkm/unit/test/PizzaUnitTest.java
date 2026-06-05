package com.lkm.unit.test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.lkm.Application;
import com.lkm.DTO.bean.PizzaOrderDTOBean;
import com.lkm.controller.PizzaController;
import com.lkm.service.PizzaService;
import com.lkm.test.utils.JSONUtils;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes=Application.class)
public class PizzaUnitTest 
{
	@Mock
	PizzaService pizzaService;
	
	@InjectMocks
	PizzaController pizzaController;
	
	protected MockMvc mockMVC;
	
	@BeforeEach
	public void mockBuilder()
	{
		MockitoAnnotations.openMocks(this);
		mockMVC = MockMvcBuilders.standaloneSetup(pizzaController).build();
	}
	
	//Testing method for add related end points
	@Test
	public void addPizza() throws Exception
	{
		String uri = "/pizza/controller/addPizza";
		PizzaOrderDTOBean pizzaBean = new PizzaOrderDTOBean("NonVegMania",2,1100.0,"7777777777");
		
		String pizzaJson = JSONUtils.convertObjectToJson(pizzaBean);
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(uri)
				.accept(MediaType.TEXT_HTML)
				.content(pizzaJson)
				.contentType(MediaType.APPLICATION_JSON);
		
		//Controller method calls service method with different object (address)
		//Need to resolve the issue
		when(pizzaService.addPizza(pizzaBean)).thenReturn(1004);
		
		ResultActions res = mockMVC.perform(request);
		MvcResult mvcResult = res.andReturn();
		
		String finalResult = mvcResult.getResponse().getContentAsString();
		int statusCode = mvcResult.getResponse().getStatus();
		
		verify(pizzaService,times(1)).addPizza(pizzaBean);
		
		Assertions.assertNotNull(finalResult);
		Assertions.assertTrue(finalResult.contains("1004"));
		Assertions.assertEquals(statusCode,HttpStatus.OK.value());
	}
	
	//Testing method for get related end points
	@SuppressWarnings("unchecked")
	@Test
	public void getPizzaDetailsTest() throws Exception
	{
		String uri = "/pizza/controller/getPizzaDetails";
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(uri)
				.accept(MediaType.APPLICATION_JSON);
		
		when(pizzaService.getPizzaDetails()).thenReturn(getPizzaMockList());
		ResultActions res = mockMVC.perform(request);
		MvcResult mvcResult = res.andReturn();
		
		String result = mvcResult.getResponse().getContentAsString();
		int statusCode = mvcResult.getResponse().getStatus();
		
		verify(pizzaService,times(1)).getPizzaDetails();
		
		List<PizzaOrderDTOBean> pizzaBeanList = JSONUtils.convertJsonToObject(result, List.class);
		Assertions.assertNotNull(pizzaBeanList);
		Assertions.assertEquals(statusCode, HttpStatus.OK.value());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getallDetailsByPizzaNameTest() throws Exception
	{
		String uri = "/pizza/controller/getDetailsByPizzaName/VegSmall";
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(uri);
		
		when(pizzaService.getallDetailsByPizzaName("VegSmall")).thenReturn(getPizzaMockList());
		ResultActions res = mockMVC.perform(request);
		MvcResult mvcResult = res.andReturn();
		
		String result = mvcResult.getResponse().getContentAsString();
		int statusCode = mvcResult.getResponse().getStatus();
		
		verify(pizzaService,times(1)).getallDetailsByPizzaName("VegSmall");
		
		List<PizzaOrderDTOBean> pizzaBeanList = JSONUtils.convertJsonToObject(result, List.class);
		Assertions.assertNotNull(pizzaBeanList);
		Assertions.assertEquals(statusCode, HttpStatus.OK.value());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getOrderDetailsByContactNumberTest() throws Exception
	{
		String uri = "/pizza/controller/getDetailsByContactNumber/1234567890";
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(uri);
		
		when(pizzaService.getOrderDetailsByContactNumber("1234567890")).thenReturn(getPizzaMockList());
		ResultActions res = mockMVC.perform(request);
		MvcResult mvcResult = res.andReturn();
		
		String result = mvcResult.getResponse().getContentAsString();
		int statusCode = mvcResult.getResponse().getStatus();
		
		verify(pizzaService,times(1)).getOrderDetailsByContactNumber("1234567890");
		
		List<PizzaOrderDTOBean> pizzaBeanList = JSONUtils.convertJsonToObject(result, List.class);
		Assertions.assertNotNull(pizzaBeanList);
		Assertions.assertEquals(statusCode, HttpStatus.OK.value());
	}
	
	//Duplicate list for testing
	public List<PizzaOrderDTOBean> getPizzaMockList()
	{
		return Arrays.asList(new PizzaOrderDTOBean("NonvegPizza",2,1100.0,"7777777777"),
				new PizzaOrderDTOBean("NonvegPizza",2,1100.0,"7777777777"));
	}
}
