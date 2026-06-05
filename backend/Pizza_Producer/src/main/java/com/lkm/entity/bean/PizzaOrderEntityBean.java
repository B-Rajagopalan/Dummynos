package com.lkm.entity.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="pizza")
public class PizzaOrderEntityBean 
{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int orderId;
	private String pizzaName;
	private Integer quantity;
	private Double bill;
	private String customerContactNumber;
	
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public String getPizzaName() {
		return pizzaName;
	}
	public void setPizzaName(String pizzaName) {
		this.pizzaName = pizzaName;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getBill() {
		return bill;
	}
	public void setBill(double bill) {
		this.bill = bill;
	}
	public String getCustomerContactNumber() {
		return customerContactNumber;
	}
	public void setCustomerContactNumber(String customerContactNumber) {
		this.customerContactNumber = customerContactNumber;
	}
	
	@Override
	public String toString() {
		return "PizzaOrderEntityBean [orderId=" + orderId + ", pizzaName=" + pizzaName + ", quantity=" + quantity
				+ ", bill=" + bill + ", customerContactNumber=" + customerContactNumber + "]";
	}
}

/* SQL CODE

drop database if exists pizza_db;

create database pizza_db;

use pizza_db;

create table IF NOT EXISTS pizza(

orderId int(11) NOT NULL AUTO_INCREMENT,

pizzaName varchar(20),quantity int,

bill double,customerContactNumber varchar(10), PRIMARY KEY(orderId)

);

insert into pizza (orderId,pizzaName,quantity,bill,customerContactNumber) values

  (1001,'VegSmall',1,200,"1234567890"),

  (1002,'VegMedium',2,400,"9543214753"),

  (1003,'VegLarge',1,600,"5843919283"),

  (1004,'NonVegSmall',2,400,"3742828282");

  commit;

*/