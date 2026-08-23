package com.company.tender.seed;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.company.tender.service.TenderQueryService;

/**
 * Publishes DB row counts when seed loader is disabled (data already from Flyway V2).
 */
@Component
public class SeedIntegrationBootstrap {

    private final JdbcTemplate jdbcTemplate;
    private final SeedIntegrationStatus seedIntegrationStatus;

    public SeedIntegrationBootstrap(JdbcTemplate jdbcTemplate, SeedIntegrationStatus seedIntegrationStatus) {
        this.jdbcTemplate = jdbcTemplate;
        this.seedIntegrationStatus = seedIntegrationStatus;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void publishCounts() {
        String tenantId = TenderQueryService.DEMO_TENANT_ID.toString();
        try {
            Long tenders = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM tms.tender WHERE tenant_id = ?::uuid",
                    Long.class,
                    tenantId);
            Long customers = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM tms.customer WHERE tenant_id = ?::uuid",
                    Long.class,
                    tenantId);
            String code = jdbcTemplate.queryForObject(
                    "SELECT code FROM tms.tenant WHERE id = ?::uuid",
                    String.class,
                    tenantId);
            seedIntegrationStatus.refreshCounts(
                    tenders != null ? tenders : 0,
                    customers != null ? customers : 0,
                    code != null ? code : "demo");
        } catch (Exception ex) {
            seedIntegrationStatus.refreshCounts(0, 0, "demo");
        }
    }
}
