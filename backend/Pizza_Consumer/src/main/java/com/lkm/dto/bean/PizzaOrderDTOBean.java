package com.lkm.dto.bean;

public class PizzaOrderDTOBean 
{
	private int orderId;
	private String pizzaName;
	private Integer quantity;
	private Double bill;
	private String customerContactNumber;
	
	public PizzaOrderDTOBean(String pizzaName, Integer quantity, Double bill, String customerContactNumber) {
		super();
		this.pizzaName = pizzaName;
		this.quantity = quantity;
		this.bill = bill;
		this.customerContactNumber = customerContactNumber;
	}
	
	public PizzaOrderDTOBean() {}
	
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
		return "PizzaOrderDTO [orderId=" + orderId + ", pizzaName=" + pizzaName + ", quantity=" + quantity + ", bill="
				+ bill + ", customerContactNumber=" + customerContactNumber + "]";
	}
}

