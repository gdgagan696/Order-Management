package com.nagarro.ordermanagement.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.jms.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.nagarro.ordermanagement.constants.CommonConstants;
import com.nagarro.ordermanagement.constants.ExceptionMessageConstants;
import com.nagarro.ordermanagement.dto.AppUserDto;
import com.nagarro.ordermanagement.dto.OrderPlaceDto;
import com.nagarro.ordermanagement.enums.OrderStatus;
import com.nagarro.ordermanagement.exception.OrderManagementException;
import com.nagarro.ordermanagement.service.AdminManagementService;
import com.nagarro.ordermanagement.service.OrderPlacementService;
import com.nagarro.ordermanagement.service.ProducerManagementService;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

@Service
public class AdminManagementServiceImpl implements AdminManagementService {

	private static final Logger LOG = LoggerFactory.getLogger(AdminManagementServiceImpl.class);

	private static List<OrderPlaceDto> adminAllOrders = new ArrayList<>();

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private OrderPlacementService orderPlacementService;

	@Autowired
	private ProducerManagementService producerManagementService;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private Queue queue;

	@Autowired
	private EurekaClient eurekaClient;

	@Override
	public List<OrderPlaceDto> getAdminAllOrders() {
		LOG.debug("Inside getAdminAllOrders method");
		if (!adminAllOrders.isEmpty()) {
			return adminAllOrders;
		}
		throw new OrderManagementException("No Orders for now.");
	}

	@Override
	public void addAdminOrder(OrderPlaceDto orderPlaceDto) {
		LOG.debug("Inside addAdminOrder method");
		adminAllOrders.add(orderPlaceDto);
	}

	@Override
	public List<AppUserDto> getNearByProducers(String producerType, Long pinCode) {
		LOG.debug("Inside getNearByProducers method,producerType:{},pinCode:{}", producerType, pinCode);
		InstanceInfo userManagementInstance = eurekaClient
				.getNextServerFromEureka(CommonConstants.USER_MANAGEMENT_APP_NAME, false);
		String userManagementBaseUrl = userManagementInstance.getHomePageUrl();
		String userByUserTypeUrl = userManagementBaseUrl + "user/" + "userByUserType/" + producerType;
		LOG.debug("userByUserTypeUrl:{}", userByUserTypeUrl);
		List<AppUserDto> nearByProducers = new ArrayList<>();
		ResponseEntity<List<AppUserDto>> userResponse = null;
		try {
			userResponse = restTemplate.exchange(userByUserTypeUrl, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<AppUserDto>>() {
					});
		}

		catch (HttpClientErrorException.BadRequest badRequest) {
			LOG.debug("Error occured while fetching response from user service.", badRequest);
			throw new OrderManagementException(badRequest.getResponseHeaders().get("errorMsg").get(0));
		} catch (Exception e) {
			LOG.debug("Error occured while fetching response from user service.", e);
			throw new RuntimeException(e);
		}
		if (Objects.nonNull(userResponse) && Objects.nonNull(userResponse.getBody())
				&& !userResponse.getBody().isEmpty()) {
			LOG.debug("Received response:{}", userResponse.getBody());
			List<AppUserDto> serviceSpecificProviders = userResponse.getBody();
			int indexOfNearByProducer = findNearByPinCode(serviceSpecificProviders, pinCode);
			LOG.debug("indexOfNearByProducer:{}", indexOfNearByProducer);
			AppUserDto finalProducer = serviceSpecificProviders.get(indexOfNearByProducer);
			LOG.debug("finalProducer:{}", finalProducer);
			nearByProducers.add(finalProducer);
			return nearByProducers;
		} else {
			throw new OrderManagementException(ExceptionMessageConstants.NO_PRODUCER_FOR_ORDER);
		}
	}

	@Override
	public OrderPlaceDto assignOrderToProducer(String producerUserName, String orderId) {
		LOG.debug("Inside assignOrderToProducer method,producerUserName:{},orderId:{}", producerUserName, orderId);
		OrderPlaceDto orderPlaceDto = adminAllOrders.stream().filter(order -> orderId.equals(order.getOrderId()))
				.findFirst()
				.orElseThrow(() -> new OrderManagementException(ExceptionMessageConstants.INVALID_ORDER_ID));
		LOG.debug("Filtered Order:{}", orderPlaceDto);
		if (!orderPlaceDto.getOrderStatus().equals(OrderStatus.SENT_FOR_ADMIN_APPROVAL.name())) {
			throw new OrderManagementException(ExceptionMessageConstants.ORDER_ALREADY_APPROVED_BY_ADMIN);
		}
		int index = adminAllOrders.indexOf(orderPlaceDto);
		InstanceInfo userManagementInstance = eurekaClient
				.getNextServerFromEureka(CommonConstants.USER_MANAGEMENT_APP_NAME, false);
		String userManagementBaseUrl = userManagementInstance.getHomePageUrl();
		String userByUserNameUrl = userManagementBaseUrl + "user/" + "getUser/" + producerUserName;
		LOG.debug("userByUserNameUrl:{}", userByUserNameUrl);
		ResponseEntity<AppUserDto> userResponse = null;
		try {
			userResponse = restTemplate.exchange(userByUserNameUrl, HttpMethod.GET, null,
					new ParameterizedTypeReference<AppUserDto>() {
					});
		} catch (HttpClientErrorException.BadRequest badRequest) {
			LOG.debug("Error occured while fetching response from user service.", badRequest);
			throw new OrderManagementException(badRequest.getResponseHeaders().get("errorMsg").get(0));
		} catch (Exception e) {
			LOG.debug("Error occured while fetching response from user service.", e);
			throw new RuntimeException(e);
		}
		if (Objects.nonNull(userResponse) && Objects.nonNull(userResponse.getBody())) {
			LOG.debug("Received Response:{}", userResponse.getBody());
			AppUserDto finalProducer = userResponse.getBody();
			orderPlaceDto.setProducerName(finalProducer.getFullName());
			orderPlaceDto.setProducerEmail(finalProducer.getEmail());
			orderPlaceDto.setOrderStatus(OrderStatus.UNDER_PRODUCER_APPROVAL.name());

			orderPlacementService.updateOrder(orderPlaceDto);
			producerManagementService.setProducerOrder(orderPlaceDto, finalProducer.getUserName());
			adminAllOrders.set(index, orderPlaceDto);
			Map<String, Object> orderInfoMap = new HashMap<>();
			prepareMapForSending(orderPlaceDto, orderInfoMap);
			jmsTemplate.convertAndSend(queue, orderInfoMap);
			LOG.debug("Sending message to queue:{}", orderInfoMap);

		} else {
			throw new OrderManagementException(ExceptionMessageConstants.NO_PRODUCER_AVAILABLE);
		}
		return orderPlaceDto;
	}

	private int findNearByPinCode(List<AppUserDto> serviceSpecificProviders, Long consumerPinCode) {
		LOG.debug("Inside findNearByPinCode method,consumerPinCode:{}", consumerPinCode);
		long distance = Math.abs(serviceSpecificProviders.get(0).getPinCode() - consumerPinCode);
		int resultIndex = 0;
		for (int i = 1; i < serviceSpecificProviders.size(); i++) {
			long tempDistance = Math.abs(serviceSpecificProviders.get(i).getPinCode() - consumerPinCode);
			if (tempDistance < distance) {
				resultIndex = i;
				distance = tempDistance;
			}
		}
		return resultIndex;
	}

	private void prepareMapForSending(OrderPlaceDto orderPlaceDto, Map<String, Object> orderInfoMap) {
		LOG.debug("Inside prepareMapForSending method");
		orderInfoMap.put("producerName", orderPlaceDto.getProducerName());
		orderInfoMap.put("consumerName", orderPlaceDto.getDeliverTo());
		orderInfoMap.put("orderId", orderPlaceDto.getOrderId());
		orderInfoMap.put("orderStatus", orderPlaceDto.getOrderStatus());
		orderInfoMap.put("producerEmail", orderPlaceDto.getProducerEmail());
		LOG.debug("After prepareMapForSending method:{}", orderInfoMap);
	}

}
