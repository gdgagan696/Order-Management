package com.nagarro.ordermanagement.controllers;

import java.util.List;

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

import com.nagarro.ordermanagement.dto.AppUserDto;
import com.nagarro.ordermanagement.dto.OrderPlaceDto;
import com.nagarro.ordermanagement.service.AdminManagementService;

@RestController
@RequestMapping("/admin")
public class AdminController {

	private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	private AdminManagementService adminManagementService;

	@GetMapping("/allOrders")
	public ResponseEntity<List<OrderPlaceDto>> getAdminAllOrders() {
		LOG.debug("Inside getAdminAllOrders method");
		return new ResponseEntity<List<OrderPlaceDto>>(adminManagementService.getAdminAllOrders(), HttpStatus.OK);
	}

	@GetMapping("/nearByProducers/{producerType}/{pinCode}")
	public ResponseEntity<List<AppUserDto>> getNearByProducers(@PathVariable String producerType,
			@PathVariable Long pinCode) {
		LOG.debug("Inside getNearByProducers method");
		return new ResponseEntity<>(adminManagementService.getNearByProducers(producerType, pinCode), HttpStatus.OK);
	}

	@PutMapping("/assignProducer/{producerUserName}/{orderId}")
	public ResponseEntity<OrderPlaceDto> assignProducer(@PathVariable String producerUserName,
			@PathVariable String orderId) {
		LOG.debug("Inside assignProducer method");
		return new ResponseEntity<>(adminManagementService.assignOrderToProducer(producerUserName, orderId),
				HttpStatus.OK);
	}
}
