package com.lkm.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;

import com.lkm.entity.bean.PizzaOrderEntityBean;

@RepositoryDefinition(idClass=Integer.class,domainClass=PizzaOrderEntityBean.class)
@Transactional
public interface PizzaDao extends JpaRepository<PizzaOrderEntityBean,Integer>
{
	List<PizzaOrderEntityBean> findByPizzaName(String pizzaName);
	
	@Query(name="PizzaDao.findByContactNumber")
	List<PizzaOrderEntityBean> findBycustomerContactNumber(String contactNumber);
}
