package com.nagarro.ordermanagement.service;

import java.util.List;

import com.nagarro.ordermanagement.dto.AppUserDto;
import com.nagarro.ordermanagement.dto.OrderPlaceDto;

public interface AdminManagementService {
	
	List<OrderPlaceDto> getAdminAllOrders();

	void addAdminOrder(OrderPlaceDto orderPlaceDto);
	
	List<AppUserDto> getNearByProducers(String producerType,Long pinCode);

	OrderPlaceDto assignOrderToProducer(String producerUserName, String orderId);

}
