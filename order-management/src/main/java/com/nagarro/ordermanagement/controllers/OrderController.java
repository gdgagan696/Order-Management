package com.nagarro.ordermanagement.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.ordermanagement.constants.CommonConstants;
import com.nagarro.ordermanagement.dto.OrderPlaceDto;
import com.nagarro.ordermanagement.service.OrderPlacementService;

@RestController
@RequestMapping("/order")
public class OrderController {

	private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private OrderPlacementService orderPlacementService;

	@PostMapping("/placeOrder")
	public ResponseEntity<OrderPlaceDto> placeOrder(@RequestBody OrderPlaceDto orderPlaceDto,
			HttpServletRequest request) {
		LOG.debug("Inside placeOrder method,orderPlaceDto:{}", orderPlaceDto);
		String userName = request.getHeader(CommonConstants.USER_NAME);
		LOG.debug("username: {}", userName);
		return new ResponseEntity<>(orderPlacementService.placeOrder(orderPlaceDto, userName), HttpStatus.CREATED);

	}

	@GetMapping("/orderInfo/{orderId}")
	public ResponseEntity<OrderPlaceDto> getOrderInfo(@PathVariable String orderId) {
		LOG.debug("Inside getOrderInfo method,orderId:{}", orderId);
		return new ResponseEntity<>(orderPlacementService.getOrderInfo(orderId), HttpStatus.OK);

	}

	@PutMapping("/updateOrderStatus/{orderId}/{orderStatus}")
	public ResponseEntity<OrderPlaceDto> updateOrderStatus(@PathVariable String orderId,
			@PathVariable String orderStatus) {
		LOG.debug("Inside updateOrderStatus method,orderId:{},orderStatus:{}", orderId, orderStatus);
		return new ResponseEntity<>(orderPlacementService.updateOrderStatus(orderId, orderStatus), HttpStatus.OK);

	}
}
