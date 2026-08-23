package com.company.tender.api;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.tender.service.TenderQueryService;

@RestController
public class DashboardController {

    private final TenderQueryService tenderQueryService;

    public DashboardController(TenderQueryService tenderQueryService) {
        this.tenderQueryService = tenderQueryService;
    }

    @GetMapping("/api/dashboard/summary")
    public Map<String, Object> summary() {
        return tenderQueryService.dashboardSummary();
    }
}
