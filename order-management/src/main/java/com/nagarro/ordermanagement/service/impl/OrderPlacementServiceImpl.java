package com.nagarro.ordermanagement.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.nagarro.ordermanagement.constants.CommonConstants;
import com.nagarro.ordermanagement.constants.ExceptionMessageConstants;
import com.nagarro.ordermanagement.dto.OrderPlaceDto;
import com.nagarro.ordermanagement.dto.ServiceDescriptionDto;
import com.nagarro.ordermanagement.enums.OrderStatus;
import com.nagarro.ordermanagement.exception.OrderManagementException;
import com.nagarro.ordermanagement.service.AdminManagementService;
import com.nagarro.ordermanagement.service.OrderPlacementService;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

@Service
public class OrderPlacementServiceImpl implements OrderPlacementService {

	private static final Logger LOG = LoggerFactory.getLogger(OrderPlacementServiceImpl.class);

	private static Map<String, OrderPlaceDto> allOrders = new HashMap<>();

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private EurekaClient eurekaClient;

	@Autowired
	private AdminManagementService adminManagementService;

	@Override
	public OrderPlaceDto placeOrder(OrderPlaceDto orderPlaceDto, String userName) {
		LOG.debug("Inside placeOrder method");
		InstanceInfo productCatalogInstance = eurekaClient
				.getNextServerFromEureka(CommonConstants.SERVICE_CATALOGUE_APP_NAME, false);
		String serviceCatalogueBaseUrl = productCatalogInstance.getHomePageUrl();
		String getServiceDescUrl = serviceCatalogueBaseUrl + "services/serviceDescInfo/"
				+ orderPlaceDto.getServiceType() + "/" + orderPlaceDto.getServiceDescriptionId();
		LOG.debug("getServiceDescUrl: {}", getServiceDescUrl);
		ResponseEntity<ServiceDescriptionDto> responseDto = null;
		try {
			responseDto = restTemplate.exchange(getServiceDescUrl, HttpMethod.GET, null,
					new ParameterizedTypeReference<ServiceDescriptionDto>() {
					});
		} catch (HttpClientErrorException.BadRequest badRequest) {
			LOG.error("Error occured while fetching service description from product service.", badRequest);
			throw new OrderManagementException(badRequest.getResponseHeaders().get("errorMsg").get(0));
		} catch (Exception e) {
			LOG.error("Error occured while fetching service description from product service.", e);
			throw new RuntimeException(e);
		}
		if (Objects.nonNull(responseDto) && Objects.nonNull(responseDto.getBody())) {
			LOG.debug("Received Response: {}", responseDto.getBody());
			ServiceDescriptionDto serviceDescriptionDto = responseDto.getBody();
			orderPlaceDto.setPerUnitPrice(serviceDescriptionDto.getPrice());
			orderPlaceDto.setTotalPrice(orderPlaceDto.getQuantity() * serviceDescriptionDto.getPrice());
			orderPlaceDto.setOrderId(UUID.randomUUID().toString());
			orderPlaceDto.setOrderStatus(OrderStatus.SENT_FOR_ADMIN_APPROVAL.name());
			orderPlaceDto.setCurrency(serviceDescriptionDto.getCurrency());
			orderPlaceDto.setDeliverTo(userName);

			allOrders.put(orderPlaceDto.getOrderId(), orderPlaceDto);
			adminManagementService.addAdminOrder(orderPlaceDto);

			return orderPlaceDto;

		} else {
			throw new OrderManagementException(ExceptionMessageConstants.INVALID_SERVICE_TYPE_DESCRIPTION_ID);
		}

	}

	@Override
	public OrderPlaceDto getOrderInfo(String orderId) {

		LOG.debug("Inside getOrderInfo method");

		if (allOrders.containsKey(orderId)) {
			return allOrders.get(orderId);
		} else {
			throw new OrderManagementException(ExceptionMessageConstants.INVALID_ORDER_ID);
		}
	}

	@Override
	public OrderPlaceDto updateOrder(OrderPlaceDto orderPlaceDto) {
		LOG.debug("Inside updateOrder method");
		if (Objects.nonNull(orderPlaceDto) && Objects.nonNull(orderPlaceDto.getOrderId())
				&& allOrders.containsKey(orderPlaceDto.getOrderId())) {
			allOrders.put(orderPlaceDto.getOrderId(), orderPlaceDto);
		}
		return orderPlaceDto;

	}

	@Override
	public OrderPlaceDto updateOrderStatus(String orderId, String orderStatus) {
		LOG.debug("Inside updateOrderStatus method");
		OrderPlaceDto orderPlaceDto = null;
		if (OrderStatus.CANCEL.name().equalsIgnoreCase(orderStatus) && allOrders.containsKey(orderId)) {
			orderPlaceDto = allOrders.get(orderId);
			orderPlaceDto.setOrderStatus(orderStatus);
		}
		else {
			throw new OrderManagementException(ExceptionMessageConstants.ERROR_UPDATE_ORDER_STATUS);
		}
		return orderPlaceDto;

	}

}
