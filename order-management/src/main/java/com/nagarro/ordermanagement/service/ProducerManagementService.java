package com.nagarro.ordermanagement.service;

import java.util.List;

import com.nagarro.ordermanagement.dto.OrderPlaceDto;

public interface ProducerManagementService {
	
	void setProducerOrder(OrderPlaceDto orderPlaceDto,String producerUserName);
	
	List<OrderPlaceDto> getProducerAllOrders(String producerUserName);

	OrderPlaceDto approveOrRejectOrder(String orderId, boolean approvalDecision, String producerName);

}
