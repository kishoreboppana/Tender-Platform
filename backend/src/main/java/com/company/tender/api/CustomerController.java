package com.company.tender.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.tender.domain.Customer;
import com.company.tender.service.CustomerQueryService;

@RestController
public class CustomerController {

    private final CustomerQueryService customerQueryService;

    public CustomerController(CustomerQueryService customerQueryService) {
        this.customerQueryService = customerQueryService;
    }

    @GetMapping("/api/customers")
    public Map<String, Object> list() {
        List<Customer> customers = customerQueryService.listDemoCustomers();
        List<Map<String, Object>> items = new ArrayList<>();

        for (Customer c : customers) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", c.getId());
            row.put("customerCode", c.getCustomerCode());
            row.put("name", c.getName());
            row.put("customerType", c.getCustomerType());
            items.add(row);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("items", items);
        body.put("total", items.size());
        return body;
    }
}
