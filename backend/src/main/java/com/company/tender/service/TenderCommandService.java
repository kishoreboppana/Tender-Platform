package com.company.tender.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.tender.api.dto.CreateTenderRequest;
import com.company.tender.domain.Customer;
import com.company.tender.domain.CustomerRepository;
import com.company.tender.domain.Tender;
import com.company.tender.domain.TenderRepository;

@Service
public class TenderCommandService {

    private static final String SEQUENCE_NAME = "TENDER_ID";
    private static final ZoneId TENANT_ZONE = ZoneId.of("Asia/Kolkata");

    private final CustomerRepository customerRepository;
    private final TenderRepository tenderRepository;
    private final JdbcTemplate jdbcTemplate;

    public TenderCommandService(
            CustomerRepository customerRepository,
            TenderRepository tenderRepository,
            JdbcTemplate jdbcTemplate) {
        this.customerRepository = customerRepository;
        this.tenderRepository = tenderRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Tender createDemoTender(CreateTenderRequest request) {
        UUID tenantId = TenderQueryService.DEMO_TENANT_ID;

        Customer customer = customerRepository.findByIdAndTenantId(request.getCustomerId(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found for demo tenant"));

        String businessId = allocateTenderBusinessId(tenantId);
        if (tenderRepository.existsByTenantIdAndTenderId(tenantId, businessId)) {
            throw new IllegalStateException("Tender ID collision: " + businessId);
        }

        Tender tender = new Tender();
        tender.setTenantId(tenantId);
        tender.setPublicId(UUID.randomUUID());
        tender.setTenderId(businessId);
        tender.setName(request.getName().trim());
        tender.setCustomer(customer);
        tender.setTenderReference(request.getTenderReference().trim());
        tender.setTenderType(request.getTenderType().trim());
        tender.setEstimatedValue(request.getEstimatedValue());
        tender.setCurrency(normalizeCurrency(request.getCurrency()));
        tender.setClosingAt(request.getClosingAt());
        tender.setSubmissionMode("PORTAL");
        tender.setTenderOwnerName(request.getTenderOwnerName().trim());
        tender.setStatus("DRAFT");
        tender.setPriority(request.getPriority().trim());
        tender.setApprovalStatus("NOT_STARTED");
        tender.setHealth("GREEN");
        tender.setCompletionPercent(resolveCompletionPercent(request.getCompletionPercent()));
        tender.setLatestStatusComment(trimToNull(request.getLatestStatusComment()));

        return tenderRepository.save(tender);
    }

    private String allocateTenderBusinessId(UUID tenantId) {
        int year = ZonedDateTime.now(TENANT_ZONE).getYear();
        Long next = jdbcTemplate.query(
                "UPDATE tms.business_sequence SET current_value = current_value + 1 "
                        + "WHERE tenant_id = ?::uuid AND sequence_name = ? AND sequence_year = ? "
                        + "RETURNING current_value",
                rs -> rs.next() ? rs.getLong(1) : null,
                tenantId.toString(),
                SEQUENCE_NAME,
                year);

        if (next == null) {
            jdbcTemplate.update(
                    "INSERT INTO tms.business_sequence (tenant_id, sequence_name, sequence_year, current_value) "
                            + "VALUES (?::uuid, ?, ?, 1) ON CONFLICT DO NOTHING",
                    tenantId.toString(),
                    SEQUENCE_NAME,
                    year);
            next = jdbcTemplate.queryForObject(
                    "UPDATE tms.business_sequence SET current_value = current_value + 1 "
                            + "WHERE tenant_id = ?::uuid AND sequence_name = ? AND sequence_year = ? "
                            + "RETURNING current_value",
                    Long.class,
                    tenantId.toString(),
                    SEQUENCE_NAME,
                    year);
        }

        if (next == null) {
            throw new IllegalStateException("Unable to allocate tender sequence for year " + year);
        }

        return String.format("TND-%d-%04d", year, next);
    }

    private static String normalizeCurrency(String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            return "INR";
        }
        return currency.trim().toUpperCase();
    }

    private static short resolveCompletionPercent(Short value) {
        if (value == null) {
            return 0;
        }
        return value;
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
