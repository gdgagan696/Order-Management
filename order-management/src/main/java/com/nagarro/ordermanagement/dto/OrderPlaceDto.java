package com.nagarro.ordermanagement.dto;

public class OrderPlaceDto {

	private String orderId;

	private String orderStatus;

	private String serviceDescriptionId;

	private String serviceType;

	private Integer quantity;

	private Double perUnitPrice;

	private Double totalPrice;

	private String currency;

	private String deliveryAddress;

	private Long pinCode;

	private String producerName;

	private String producerEmail;

	private String deliverTo;

	private String deliverToEmail;

	private String workDescription;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getServiceDescriptionId() {
		return serviceDescriptionId;
	}

	public void setServiceDescriptionId(String serviceDescriptionId) {
		this.serviceDescriptionId = serviceDescriptionId;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getPerUnitPrice() {
		return perUnitPrice;
	}

	public void setPerUnitPrice(Double perUnitPrice) {
		this.perUnitPrice = perUnitPrice;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public Long getPinCode() {
		return pinCode;
	}

	public void setPinCode(Long pinCode) {
		this.pinCode = pinCode;
	}

	public String getProducerName() {
		return producerName;
	}

	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}

	public String getDeliverTo() {
		return deliverTo;
	}

	public void setDeliverTo(String deliverTo) {
		this.deliverTo = deliverTo;
	}

	public String getDeliverToEmail() {
		return deliverToEmail;
	}

	public void setDeliverToEmail(String deliverToEmail) {
		this.deliverToEmail = deliverToEmail;
	}

	public String getProducerEmail() {
		return producerEmail;
	}

	public void setProducerEmail(String producerEmail) {
		this.producerEmail = producerEmail;
	}

	public String getWorkDescription() {
		return workDescription;
	}

	public void setWorkDescription(String workDescription) {
		this.workDescription = workDescription;
	}

	@Override
	public String toString() {
		return "OrderPlaceDto [orderId=" + orderId + ", orderStatus=" + orderStatus + ", serviceDescriptionId="
				+ serviceDescriptionId + ", serviceType=" + serviceType + ", quantity=" + quantity + ", perUnitPrice="
				+ perUnitPrice + ", totalPrice=" + totalPrice + ", currency=" + currency + ", deliveryAddress="
				+ deliveryAddress + ", pinCode=" + pinCode + ", producerName=" + producerName + ", producerEmail="
				+ producerEmail + ", deliverTo=" + deliverTo + ", deliverToEmail=" + deliverToEmail
				+ ", workDescription=" + workDescription + "]";
	}

}
