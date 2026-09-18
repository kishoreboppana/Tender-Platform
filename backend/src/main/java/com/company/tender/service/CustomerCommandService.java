package com.company.tender.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.tender.api.dto.CreateCustomerRequest;
import com.company.tender.domain.Customer;
import com.company.tender.domain.CustomerRepository;

@Service
public class CustomerCommandService {

    private final CustomerRepository customerRepository;

    public CustomerCommandService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer createDemoCustomer(CreateCustomerRequest request) {
        UUID tenantId = TenderQueryService.DEMO_TENANT_ID;
        String customerCode = request.getCustomerCode().trim().toUpperCase();

        if (customerRepository.existsByTenantIdAndCustomerCode(tenantId, customerCode)) {
            throw new IllegalArgumentException("Customer code already exists: " + customerCode);
        }

        Customer customer = new Customer();
        customer.setTenantId(tenantId);
        customer.setCustomerCode(customerCode);
        customer.setName(request.getName().trim());
        customer.setCustomerType(request.getCustomerType().trim().toUpperCase());
        customer.setContactEmail(trimToNull(request.getContactEmail()));
        customer.setActive(request.getActive() == null || request.getActive());

        return customerRepository.save(customer);
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
