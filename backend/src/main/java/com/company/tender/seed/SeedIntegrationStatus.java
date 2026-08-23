package com.company.tender.seed;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.tender.config.SeedProperties;
import com.company.tender.service.TenderQueryService;

/**
 * Runtime snapshot of demo seed integration for health and diagnostics APIs.
 */
@Component
public class SeedIntegrationStatus {

    private final SeedProperties seedProperties;
    private volatile String tenantCode = "demo";
    private volatile long tenderCount = 0;
    private volatile long customerCount = 0;
    private volatile boolean dataLoaded = false;

    public SeedIntegrationStatus(SeedProperties seedProperties) {
        this.seedProperties = seedProperties;
    }

    public void markLoaded(UUID tenantId, long tenders, long customers, String code) {
        this.tenantCode = code;
        this.tenderCount = tenders;
        this.customerCount = customers;
        this.dataLoaded = tenders > 0;
    }

    public void refreshCounts(long tenders, long customers, String code) {
        this.tenantCode = code;
        this.tenderCount = tenders;
        this.customerCount = customers;
        this.dataLoaded = tenders > 0;
    }

    public String getDataLocation() {
        return seedProperties.getDataLocation();
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public long getTenderCount() {
        return tenderCount;
    }

    public long getCustomerCount() {
        return customerCount;
    }

    public boolean isDataLoaded() {
        return dataLoaded;
    }

    public UUID getDemoTenantId() {
        return TenderQueryService.DEMO_TENANT_ID;
    }
}
