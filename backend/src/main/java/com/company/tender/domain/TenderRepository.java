package com.company.tender.domain;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TenderRepository extends JpaRepository<Tender, Long> {

    List<Tender> findByTenantIdOrderByClosingAtAsc(UUID tenantId);

    long countByTenantIdAndStatusNotIn(UUID tenantId, List<String> terminalStatuses);

    long countByTenantIdAndApprovalStatus(UUID tenantId, String approvalStatus);

    @Query("SELECT COUNT(t) FROM Tender t WHERE t.tenantId = :tenantId AND t.health = 'RED'")
    long countRedHealth(@Param("tenantId") UUID tenantId);

    boolean existsByTenantIdAndTenderId(UUID tenantId, String tenderId);
}
