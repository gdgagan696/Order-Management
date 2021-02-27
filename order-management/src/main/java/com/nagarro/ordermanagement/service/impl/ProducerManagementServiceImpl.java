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
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.nagarro.ordermanagement.constants.ExceptionMessageConstants;
import com.nagarro.ordermanagement.dto.OrderPlaceDto;
import com.nagarro.ordermanagement.enums.OrderStatus;
import com.nagarro.ordermanagement.exception.OrderManagementException;
import com.nagarro.ordermanagement.service.OrderPlacementService;
import com.nagarro.ordermanagement.service.ProducerManagementService;

@Service
public class ProducerManagementServiceImpl implements ProducerManagementService {

	private static final Logger LOG = LoggerFactory.getLogger(ProducerManagementServiceImpl.class);

	private static Map<String, List<OrderPlaceDto>> allProducersOrders = new HashMap<>();

	@Autowired
	private OrderPlacementService orderPlacementService;

	@Autowired
	private Queue queue;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Override
	public void setProducerOrder(OrderPlaceDto orderPlaceDto, String producerUserName) {
		LOG.debug("Inside setProducerOrder method.");
		if (Objects.nonNull(orderPlaceDto) && Objects.nonNull(producerUserName)) {
			if (allProducersOrders.containsKey(producerUserName)) {
				LOG.debug("Inside setProducerOrder method if condition.");
				allProducersOrders.get(producerUserName).add(orderPlaceDto);
			} else {
				LOG.debug("Inside setProducerOrder method else condition.");
				List<OrderPlaceDto> orderPlaceDtos = new ArrayList<>();
				orderPlaceDtos.add(orderPlaceDto);
				allProducersOrders.put(producerUserName, orderPlaceDtos);
			}
		}

	}

	@Override
	public List<OrderPlaceDto> getProducerAllOrders(String producerUserName) {
		LOG.debug("Inside getProducerAllOrders method.");
		if (Objects.nonNull(producerUserName) && allProducersOrders.containsKey(producerUserName)) {
			return allProducersOrders.get(producerUserName);
		} else {
			throw new OrderManagementException(ExceptionMessageConstants.NO_ORDER_AVAILABLE);
		}

	}

	@Override
	public OrderPlaceDto approveOrRejectOrder(String orderId, boolean approvalDecision, String producerUserName) {
		LOG.debug("Inside approveOrRejectOrder method. orderId:{},decision:{},producerUserName:{}", orderId,
				approvalDecision, producerUserName);
		if (allProducersOrders.containsKey(producerUserName)) {
			List<OrderPlaceDto> producerOrders = allProducersOrders.get(producerUserName);
			LOG.debug("ProducerOrders:{}", producerOrders);
			OrderPlaceDto orderPlaceDto = producerOrders.stream().filter(order -> order.getOrderId().equals(orderId))
					.findFirst()
					.orElseThrow(() -> new OrderManagementException(ExceptionMessageConstants.INVALID_ORDER_ID));
			if (!orderPlaceDto.getOrderStatus().equals(OrderStatus.UNDER_PRODUCER_APPROVAL.name())) {
				throw new OrderManagementException(ExceptionMessageConstants.ORDER_ALREADY_APPROVED_BY_PRODUCER);
			}
			if (approvalDecision) {
				orderPlaceDto.setOrderStatus(OrderStatus.CONFIRMED.name());
			} else {
				orderPlaceDto.setOrderStatus(OrderStatus.CANCEL.name());
			}

			orderPlacementService.updateOrder(orderPlaceDto);
			Map<String, Object> orderInfoMap = new HashMap<>();
			prepareMapForSending(orderPlaceDto, orderInfoMap);
			jmsTemplate.convertAndSend(queue, orderInfoMap);
			LOG.debug("Send to queue:{}", orderInfoMap);
			return orderPlaceDto;
		} else {
			throw new OrderManagementException(ExceptionMessageConstants.NO_ORDER_AVAILABLE);
		}

	}

	private void prepareMapForSending(OrderPlaceDto orderPlaceDto, Map<String, Object> orderInfoMap) {
		LOG.debug("Inside prepareMapForSending method");
		orderInfoMap.put("producerName", orderPlaceDto.getProducerName());
		orderInfoMap.put("producerEmail", orderPlaceDto.getProducerEmail());
		orderInfoMap.put("consumerName", orderPlaceDto.getDeliverTo());
		orderInfoMap.put("consumerEmail", orderPlaceDto.getDeliverToEmail());
		orderInfoMap.put("orderId", orderPlaceDto.getOrderId());
		orderInfoMap.put("orderStatus", orderPlaceDto.getOrderStatus());
		orderInfoMap.put("workDescription",
				Objects.nonNull(orderPlaceDto.getWorkDescription()) ? orderPlaceDto.getWorkDescription() : "");
		LOG.debug("After prepareMapForSending method:{}", orderInfoMap);

	}

}
