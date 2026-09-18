package com.company.tender.api;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.company.tender.api.dto.CreateCustomerRequest;
import com.company.tender.domain.Customer;
import com.company.tender.service.CustomerCommandService;
import com.company.tender.service.CustomerQueryService;

@RestController
public class CustomerController {

    private final CustomerQueryService customerQueryService;
    private final CustomerCommandService customerCommandService;

    public CustomerController(
            CustomerQueryService customerQueryService,
            CustomerCommandService customerCommandService) {
        this.customerQueryService = customerQueryService;
        this.customerCommandService = customerCommandService;
    }

    @GetMapping("/api/customers")
    public Map<String, Object> list() {
        return CustomerResponseMapper.listResponse(customerQueryService.listDemoCustomers());
    }

    @PostMapping("/api/customers")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@Valid @RequestBody CreateCustomerRequest request) {
        Customer customer = customerCommandService.createDemoCustomer(request);
        Map<String, Object> body = new HashMap<>();
        body.put("item", CustomerResponseMapper.toRow(customer));
        return body;
    }
}
