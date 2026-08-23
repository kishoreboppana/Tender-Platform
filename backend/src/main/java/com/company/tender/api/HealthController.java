package com.company.tender.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.tender.seed.SeedIntegrationStatus;

@RestController
public class HealthController {

    private final JdbcTemplate jdbcTemplate;
    private final SeedIntegrationStatus seedIntegrationStatus;

    @Autowired
    public HealthController(JdbcTemplate jdbcTemplate, SeedIntegrationStatus seedIntegrationStatus) {
        this.jdbcTemplate = jdbcTemplate;
        this.seedIntegrationStatus = seedIntegrationStatus;
    }

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "UP");
        body.put("phase", "0-db");
        try {
            Integer tableCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'tms'",
                    Integer.class);
            body.put("database", "connected");
            body.put("schema", "tms");
            body.put("tablesInSchema", tableCount);
            body.put("seedDataSource", seedIntegrationStatus.getDataLocation());
            body.put("demoTenant", seedIntegrationStatus.getTenantCode());
            body.put("tendersLoaded", seedIntegrationStatus.getTenderCount());
            body.put("customersLoaded", seedIntegrationStatus.getCustomerCount());
            body.put("seedIntegrated", seedIntegrationStatus.isDataLoaded());
        } catch (Exception ex) {
            body.put("database", "error");
            body.put("message", ex.getMessage());
        }
        return body;
    }
}
