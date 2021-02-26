package com.nagarro.ordermanagement.dto;

public class ServiceDescriptionDto {

	private String serviceDescriptionId;

	private String serviceName;

	private String serviceDescription;

	private Double price;

	private String currency;

	private String serviceId;

	public String getServiceDescriptionId() {
		return serviceDescriptionId;
	}

	public void setServiceDescriptionId(String serviceDescriptionId) {
		this.serviceDescriptionId = serviceDescriptionId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	@Override
	public String toString() {
		return "ServiceDescriptionDto [serviceDescriptionId=" + serviceDescriptionId + ", serviceName=" + serviceName
				+ ", serviceDescription=" + serviceDescription + ", price=" + price + ", currency=" + currency
				+ ", serviceId=" + serviceId + "]";
	}
	
	

}
