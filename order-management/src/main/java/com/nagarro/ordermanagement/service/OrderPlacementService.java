package com.nagarro.ordermanagement.service;

import com.nagarro.ordermanagement.dto.OrderPlaceDto;

public interface OrderPlacementService {

	OrderPlaceDto placeOrder(OrderPlaceDto orderPlaceDto,String userName);

	OrderPlaceDto getOrderInfo(String orderId);

	OrderPlaceDto updateOrder(OrderPlaceDto orderPlaceDto);
	
	OrderPlaceDto updateOrderStatus(String orderId,String orderStatus);


}
