package com.company.tender.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.company.tender.domain.Customer;

final class CustomerResponseMapper {

    private CustomerResponseMapper() {
    }

    static Map<String, Object> toRow(Customer customer) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", customer.getId());
        row.put("customerCode", customer.getCustomerCode());
        row.put("name", customer.getName());
        row.put("customerType", customer.getCustomerType());
        row.put("contactEmail", customer.getContactEmail());
        row.put("active", customer.isActive());
        return row;
    }

    static Map<String, Object> listResponse(List<Customer> customers) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (Customer customer : customers) {
            items.add(toRow(customer));
        }
        Map<String, Object> body = new HashMap<>();
        body.put("items", items);
        body.put("total", items.size());
        return body;
    }
}
