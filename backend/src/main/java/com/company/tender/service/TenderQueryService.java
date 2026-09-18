package com.company.tender.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.company.tender.domain.Tenant;
import com.company.tender.domain.TenantRepository;
import com.company.tender.domain.Tender;
import com.company.tender.domain.TenderRepository;
import com.company.tender.seed.SeedDashboardDefaults;

@Service
@Transactional(readOnly = true)
public class TenderQueryService {

    public static final UUID DEMO_TENANT_ID =
            UUID.fromString("a0000000-0000-4000-8000-000000000001");

    private static final List<String> TERMINAL_STATUSES = Arrays.asList(
            "BID_DECLINED", "AWARDED", "LOST", "CANCELLED", "CLOSED");

    private final TenantRepository tenantRepository;
    private final TenderRepository tenderRepository;
    private final SeedDashboardDefaults seedDashboardDefaults;

    public TenderQueryService(
            TenantRepository tenantRepository,
            TenderRepository tenderRepository,
            SeedDashboardDefaults seedDashboardDefaults) {
        this.tenantRepository = tenantRepository;
        this.tenderRepository = tenderRepository;
        this.seedDashboardDefaults = seedDashboardDefaults;
    }

    public Tenant getDemoTenant() {
        return tenantRepository.findById(DEMO_TENANT_ID)
                .orElseThrow(() -> new IllegalStateException("Demo tenant not found — run Flyway migrations"));
    }

    public List<Tender> listDemoTenders() {
        return tenderRepository.findByTenantIdOrderByClosingAtAsc(DEMO_TENANT_ID);
    }

    public Tender getDemoTenderByBusinessId(String tenderId) {
        return tenderRepository.findByTenantIdAndTenderId(DEMO_TENANT_ID, tenderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Tender not found: " + tenderId));
    }

    public Map<String, Object> dashboardSummary() {
        Tenant tenant = getDemoTenant();
        long active = tenderRepository.countByTenantIdAndStatusNotIn(DEMO_TENANT_ID, TERMINAL_STATUSES);
        long pendingApprovals = tenderRepository.countByTenantIdAndApprovalStatus(DEMO_TENANT_ID, "PENDING");
        long redRisks = tenderRepository.countRedHealth(DEMO_TENANT_ID);

        Map<String, Object> body = new HashMap<>();
        body.put("activeTenders", active);
        body.put("closingToday", countClosingToday());
        body.put("closingIn7Days", seedDashboardDefaults.getClosingIn7Days());
        body.put("pendingApprovals", pendingApprovals);
        body.put("missingMandatoryDocs", seedDashboardDefaults.getMissingMandatoryDocs());
        body.put("overdueActions", seedDashboardDefaults.getOverdueActions());
        body.put("redRisks", redRisks);
        body.put("winRatePercent", seedDashboardDefaults.getWinRatePercent());
        body.put("digestStatus", seedDashboardDefaults.getDigestStatus());
        body.put("digestLastRun", seedDashboardDefaults.getDigestLastRun());
        body.put("tenantName", tenant.getName());
        return body;
    }

    private long countClosingToday() {
        // Simplified for initial setup — full timezone logic in Phase 1
        return listDemoTenders().stream()
                .filter(t -> "RED".equals(t.getHealth()) && "IN_PREPARATION".equals(t.getStatus()))
                .count();
    }
}
