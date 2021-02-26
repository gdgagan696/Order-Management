package com.nagarro.ordermanagement.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.ordermanagement.constants.CommonConstants;
import com.nagarro.ordermanagement.dto.OrderPlaceDto;
import com.nagarro.ordermanagement.service.ProducerManagementService;

@RestController
@RequestMapping("/producer")
public class ProducerController {

	private static final Logger LOG = LoggerFactory.getLogger(ProducerController.class);

	@Autowired
	private ProducerManagementService producerManagementService;

	@GetMapping("/allOrders/{producerUserName}")
	public ResponseEntity<List<OrderPlaceDto>> getProducerAllOrders(
			@PathVariable(required = false) String producerUserName, HttpServletRequest request) {

		LOG.debug("Inside getProducerAllOrders,producerName:{}", producerUserName);
		if (producerUserName.isEmpty()) {
			producerUserName = request.getHeader(CommonConstants.USER_NAME);
			LOG.debug("Inside getProducerAllOrders,producerName:{}", producerUserName);
		}
		return new ResponseEntity<>(producerManagementService.getProducerAllOrders(producerUserName), HttpStatus.OK);

	}

	@PutMapping("/approveRejectOrder/{orderId}/{approvalDecision}/{producerUserName}")
	public ResponseEntity<OrderPlaceDto> approveOrRejectOrder(@PathVariable String orderId,
			@PathVariable boolean approvalDecision, @PathVariable(required = false) String producerUserName,
			HttpServletRequest request) {
		LOG.debug("Inside approveOrRejectOrder,producerName:{}", producerUserName);

		if (producerUserName.isEmpty()) {
			producerUserName = request.getHeader(CommonConstants.USER_NAME);
			LOG.debug("Inside approveOrRejectOrder,producerName:{}", producerUserName);
		}
		return new ResponseEntity<>(
				producerManagementService.approveOrRejectOrder(orderId, approvalDecision, producerUserName),
				HttpStatus.OK);

	}

}
