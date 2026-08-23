package com.company.tender.seed;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.company.tender.config.SeedProperties;
import com.company.tender.domain.TenantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Loads demo rows from {@code classpath:db/seed/demo-data.json} after Flyway creates tables.
 */
@Component
@ConditionalOnProperty(prefix = "tender.seed", name = "enabled", havingValue = "true")
public class DatabaseSeedLoader {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeedLoader.class);

    private final SeedProperties seedProperties;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;
    private final TenantRepository tenantRepository;
    private final SeedDashboardDefaults seedDashboardDefaults;
    private final SeedIntegrationStatus seedIntegrationStatus;

    public DatabaseSeedLoader(
            SeedProperties seedProperties,
            ResourceLoader resourceLoader,
            ObjectMapper objectMapper,
            JdbcTemplate jdbcTemplate,
            TenantRepository tenantRepository,
            SeedDashboardDefaults seedDashboardDefaults,
            SeedIntegrationStatus seedIntegrationStatus) {
        this.seedProperties = seedProperties;
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.tenantRepository = tenantRepository;
        this.seedDashboardDefaults = seedDashboardDefaults;
        this.seedIntegrationStatus = seedIntegrationStatus;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void loadOnStartup() throws IOException {
        DemoSeedData data = readSeedFile();
        UUID tenantId = UUID.fromString(data.getTenant().getId());

        if (tenantRepository.existsById(tenantId) && !seedProperties.isForce()) {
            log.info("Demo tenant already present — skipping row inserts from seed file");
            refreshIntegrationStatus(tenantId, data.getTenant().getCode());
            return;
        }

        insertTenant(data.getTenant());
        insertAppUsers(tenantId, data);
        insertCustomers(tenantId, data);
        insertBusinessSequences(tenantId, data);
        Map<String, Long> customerIds = loadCustomerIds(tenantId);
        insertTenders(tenantId, data, customerIds);
        insertAuditEvents(tenantId, data);

        refreshIntegrationStatus(tenantId, data.getTenant().getCode());
        log.info("Loaded demo seed data from {}", seedProperties.getDataLocation());
    }

    private void refreshIntegrationStatus(UUID tenantId, String tenantCode) {
        Long tenders = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tms.tender WHERE tenant_id = ?::uuid",
                Long.class,
                tenantId.toString());
        Long customers = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tms.customer WHERE tenant_id = ?::uuid",
                Long.class,
                tenantId.toString());
        seedIntegrationStatus.refreshCounts(
                tenders != null ? tenders : 0,
                customers != null ? customers : 0,
                tenantCode);
    }

    private DemoSeedData readSeedFile() throws IOException {
        Resource resource = resourceLoader.getResource(seedProperties.getDataLocation());
        try (InputStream in = resource.getInputStream()) {
            return objectMapper.readValue(in, DemoSeedData.class);
        }
    }

    private void insertTenant(DemoSeedData.TenantSeed tenant) {
        jdbcTemplate.update(
                "INSERT INTO tms.tenant (id, code, name, status, timezone, database_mode) "
                        + "VALUES (?::uuid, ?, ?, ?, ?, ?) ON CONFLICT (id) DO NOTHING",
                tenant.getId(),
                tenant.getCode(),
                tenant.getName(),
                tenant.getStatus(),
                tenant.getTimezone(),
                tenant.getDatabaseMode());
    }

    private void insertAppUsers(UUID tenantId, DemoSeedData data) {
        for (DemoSeedData.AppUserSeed user : data.getAppUsers()) {
            jdbcTemplate.update(
                    "INSERT INTO tms.app_user (tenant_id, email, display_name, active) "
                            + "VALUES (?::uuid, ?, ?, ?) ON CONFLICT (tenant_id, email) DO NOTHING",
                    tenantId.toString(),
                    user.getEmail(),
                    user.getDisplayName(),
                    user.isActive());
        }
    }

    private void insertCustomers(UUID tenantId, DemoSeedData data) {
        for (DemoSeedData.CustomerSeed customer : data.getCustomers()) {
            jdbcTemplate.update(
                    "INSERT INTO tms.customer (tenant_id, customer_code, name, customer_type, contact_email, active) "
                            + "VALUES (?::uuid, ?, ?, ?, ?, ?) "
                            + "ON CONFLICT (tenant_id, customer_code) DO NOTHING",
                    tenantId.toString(),
                    customer.getCustomerCode(),
                    customer.getName(),
                    customer.getCustomerType(),
                    customer.getContactEmail(),
                    customer.isActive());
        }
    }

    private void insertBusinessSequences(UUID tenantId, DemoSeedData data) {
        for (DemoSeedData.BusinessSequenceSeed sequence : data.getBusinessSequences()) {
            jdbcTemplate.update(
                    "INSERT INTO tms.business_sequence (tenant_id, sequence_name, sequence_year, current_value) "
                            + "VALUES (?::uuid, ?, ?, ?) "
                            + "ON CONFLICT (tenant_id, sequence_name, sequence_year) DO NOTHING",
                    tenantId.toString(),
                    sequence.getSequenceName(),
                    sequence.getSequenceYear(),
                    sequence.getCurrentValue());
        }
    }

    private Map<String, Long> loadCustomerIds(UUID tenantId) {
        Map<String, Long> ids = new HashMap<>();
        jdbcTemplate.query(
                "SELECT customer_code, id FROM tms.customer WHERE tenant_id = ?::uuid",
                rs -> {
                    ids.put(rs.getString("customer_code"), rs.getLong("id"));
                },
                tenantId.toString());
        return ids;
    }

    private void insertTenders(UUID tenantId, DemoSeedData data, Map<String, Long> customerIds) {
        for (DemoSeedData.TenderSeed tender : data.getTenders()) {
            Long customerId = customerIds.get(tender.getCustomerCode());
            if (customerId == null) {
                throw new IllegalStateException(
                        "Customer not found for tender " + tender.getTenderId()
                                + " (code " + tender.getCustomerCode() + ")");
            }
            jdbcTemplate.update(
                    "INSERT INTO tms.tender ("
                            + "tenant_id, tender_id, name, customer_id, tender_reference, tender_type, "
                            + "estimated_value, currency, closing_at, submission_mode, tender_owner_name, "
                            + "status, priority, approval_status, health, completion_percent, latest_status_comment"
                            + ") VALUES ("
                            + "?::uuid, ?, ?, ?, ?, ?, ?, ?, ?::timestamptz, ?, ?, ?, ?, ?, ?, ?, ?"
                            + ") ON CONFLICT (tenant_id, tender_id) DO NOTHING",
                    tenantId.toString(),
                    tender.getTenderId(),
                    tender.getName(),
                    customerId,
                    tender.getTenderReference(),
                    tender.getTenderType(),
                    tender.getEstimatedValue(),
                    tender.getCurrency(),
                    tender.getClosingAt(),
                    tender.getSubmissionMode(),
                    tender.getTenderOwnerName(),
                    tender.getStatus(),
                    tender.getPriority(),
                    tender.getApprovalStatus(),
                    tender.getHealth(),
                    tender.getCompletionPercent(),
                    tender.getLatestStatusComment());
        }
    }

    private void insertAuditEvents(UUID tenantId, DemoSeedData data) {
        for (DemoSeedData.AuditEventSeed event : data.getAuditEvents()) {
            jdbcTemplate.update(
                    "INSERT INTO tms.audit_event (tenant_id, event_type, entity_type, entity_id, actor, detail) "
                            + "VALUES (?::uuid, ?, ?, ?, ?, ?)",
                    tenantId.toString(),
                    event.getEventType(),
                    event.getEntityType(),
                    event.getEntityId(),
                    event.getActor(),
                    event.getDetail());
        }
    }
}
