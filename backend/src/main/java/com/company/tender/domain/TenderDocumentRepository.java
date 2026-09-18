package com.company.tender.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TenderDocumentRepository extends JpaRepository<TenderDocument, Long> {

    List<TenderDocument> findByTenantIdAndTender_IdOrderByUploadedAtDesc(UUID tenantId, Long tenderId);

    Optional<TenderDocument> findByIdAndTenantIdAndTender_Id(Long id, UUID tenantId, Long tenderId);
}
