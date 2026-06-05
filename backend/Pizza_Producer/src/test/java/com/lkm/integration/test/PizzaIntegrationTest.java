package com.lkm.integration.test;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.lkm.Application;
import com.lkm.DTO.bean.PizzaOrderDTOBean;
import com.lkm.test.utils.JSONUtils;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes=Application.class)
@Transactional
@WebAppConfiguration
public class PizzaIntegrationTest 
{
	@Autowired
	WebApplicationContext webApplicationContext;
	
	protected MockMvc mockMVC;
	
	@BeforeEach
	public void mockBuilder()
	{
		mockMVC = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	//Testing method for add related end points
	@Test
	public void addPizzaTest() throws Exception
	{
		String uri = "/pizza/controller/addPizza";
		PizzaOrderDTOBean pizzaBean = new PizzaOrderDTOBean("NonvegMania",2,1100.0,"7777777777");
		String pizzaJson = JSONUtils.convertObjectToJson(pizzaBean);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(uri)
				.accept(MediaType.TEXT_HTML)
				.content(pizzaJson)
				.contentType(MediaType.APPLICATION_JSON);
		
		ResultActions res = mockMVC.perform(request);
		MvcResult mvcResult = res.andReturn();
		
		String finalResult = mvcResult.getResponse().getContentAsString();
		
		int actualStatus = mvcResult.getResponse().getStatus();
		Assertions.assertNotNull(finalResult);
		Assertions.assertTrue(finalResult.contains("Pizza added successfully with id number"));
		Assertions.assertEquals(actualStatus, HttpStatus.OK.value());
		
	}
	
	//Testing method for get related end points
	@Test
	@SuppressWarnings("unchecked")
	public void getPizzaDetailsTest() throws Exception
	{
		String uri = "/pizza/controller/getPizzaDetails";
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(uri)
				.accept(MediaType.APPLICATION_JSON);
		
		ResultActions res = mockMVC.perform(request);
		MvcResult mvcResult = res.andReturn();
		
		String result = mvcResult.getResponse().getContentAsString();
		int actualStatus = mvcResult.getResponse().getStatus();
		List<PizzaOrderDTOBean> pizzaBeanList = JSONUtils.convertJsonToObject(result, List.class);
		Assertions.assertNotNull(pizzaBeanList);
		Assertions.assertEquals(actualStatus, HttpStatus.OK.value());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void getallDetailsByPizzaNameTest() throws Exception
	{
		String uri = "/pizza/controller/getDetailsByPizzaName/VegSmall";
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(uri)
				.accept(MediaType.APPLICATION_JSON);
		
		ResultActions res = mockMVC.perform(request);
		MvcResult mvcResult = res.andReturn();
		
		String result = mvcResult.getResponse().getContentAsString();
		System.out.println("Helooooooo"+result);
		int actualStatus = mvcResult.getResponse().getStatus();
		List<PizzaOrderDTOBean> pizzaBeanList = JSONUtils.convertJsonToObject(result, List.class);
		Assertions.assertNotNull(pizzaBeanList);
		Assertions.assertEquals(actualStatus, HttpStatus.OK.value());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void getOrderDetailsByContactNumber() throws Exception
	{
		String uri = "/pizza/controller/getDetailsByContactNumber/1234567890";
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(uri)
				.accept(MediaType.APPLICATION_JSON);
		
		ResultActions res = mockMVC.perform(request);
		MvcResult mvcResult = res.andReturn();
		
		String result = mvcResult.getResponse().getContentAsString();
		int actualStatus = mvcResult.getResponse().getStatus();
		List<PizzaOrderDTOBean> pizzaBeanList = JSONUtils.convertJsonToObject(result, List.class);
		Assertions.assertNotNull(pizzaBeanList);
		Assertions.assertEquals(actualStatus, HttpStatus.OK.value());
	}
}
