package com.app.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.app.dao.PizzaDao;

import com.app.DTO.bean.PizzaOrderDTOBean;
import com.app.entity.bean.PizzaOrderEntityBean;

@Service
public class PizzaService
{
	@Autowired
	private PizzaDao pizzaDao;
	
	@CacheEvict(value="pizzas", allEntries = true)
	public int addPizza(PizzaOrderDTOBean pizzaBean)
	{
		PizzaOrderEntityBean pizzaEntity = new PizzaOrderEntityBean();
		int idNumber=0;
		
		pizzaEntity = copyBeanToEntity(pizzaBean);
		pizzaEntity.setOrderId(0); // Force auto-increment insert of a new order even if an orderId was supplied in the payload
		idNumber = pizzaDao.save(pizzaEntity).getOrderId();
		
		return idNumber;
	}

	@Cacheable(value="pizzas")
	public List<PizzaOrderDTOBean> getPizzaDetails()
	{
		List<PizzaOrderDTOBean> pizzaBeanList = new ArrayList<PizzaOrderDTOBean>();
		Collection<PizzaOrderEntityBean> pizzaEntity = pizzaDao.findAll();
		
		if(pizzaEntity!=null)
		{
			pizzaEntity.stream().map(PizzaService::copyEntitytoBean).
			forEach(pizzaBeanList::add);
		}
		System.out.println("Retrieved data from db");
		return pizzaBeanList;
	}
	
	public List<PizzaOrderDTOBean> getallDetailsByPizzaName(String pizzaName)
	{
		List<PizzaOrderDTOBean> pizzaBeanList = new ArrayList<PizzaOrderDTOBean>();
		List<PizzaOrderEntityBean> pizzaEntity = pizzaDao.findByPizzaName(pizzaName);
		
		if(pizzaEntity!=null)
		{
			pizzaEntity.stream().map(PizzaService::copyEntitytoBean).
			forEach(pizzaBeanList::add);
		}
		
		return pizzaBeanList;
	}
	
	public List<PizzaOrderDTOBean> getOrderDetailsByContactNumber(String contactNumber) 
	{
		List<PizzaOrderDTOBean> pizzaBeanList = new ArrayList<PizzaOrderDTOBean>();
		List<PizzaOrderEntityBean> pizzaEntity = pizzaDao.findBycustomerContactNumber(contactNumber);
		
		if(pizzaEntity!=null)
		{
			pizzaEntity.stream().map(PizzaService::copyEntitytoBean).
			forEach(pizzaBeanList::add);
		}
		
		return pizzaBeanList;
	}
	
	public static PizzaOrderEntityBean copyBeanToEntity(PizzaOrderDTOBean pizzaBean)
	{
		PizzaOrderEntityBean pizzaEntity = new PizzaOrderEntityBean();
		BeanUtils.copyProperties(pizzaBean, pizzaEntity);
		return pizzaEntity;
	}
	
	public static PizzaOrderDTOBean copyEntitytoBean(PizzaOrderEntityBean pizzaEntity)
	{
		PizzaOrderDTOBean pizzaBean = new PizzaOrderDTOBean();
		BeanUtils.copyProperties(pizzaEntity, pizzaBean);
		return pizzaBean;
	}
}
