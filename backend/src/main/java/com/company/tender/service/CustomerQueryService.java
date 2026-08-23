package com.company.tender.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.tender.domain.Customer;
import com.company.tender.domain.CustomerRepository;

@Service
@Transactional(readOnly = true)
public class CustomerQueryService {

    private final CustomerRepository customerRepository;

    public CustomerQueryService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> listDemoCustomers() {
        return customerRepository.findByTenantIdAndActiveTrueOrderByNameAsc(
                TenderQueryService.DEMO_TENANT_ID);
    }
}
