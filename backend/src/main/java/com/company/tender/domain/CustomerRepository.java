package com.company.tender.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByTenantIdAndActiveTrueOrderByNameAsc(UUID tenantId);

    Optional<Customer> findByIdAndTenantId(Long id, UUID tenantId);
}
