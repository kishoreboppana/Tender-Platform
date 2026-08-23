package com.company.tender.seed;

import org.springframework.stereotype.Component;

import com.company.tender.seed.DemoSeedData.DashboardDefaultsSeed;

/**
 * Dashboard KPI defaults loaded from {@code db/seed/demo-data.json}.
 */
@Component
public class SeedDashboardDefaults {

    private DashboardDefaultsSeed defaults = createBuiltInDefaults();

    private static DashboardDefaultsSeed createBuiltInDefaults() {
        DashboardDefaultsSeed d = new DashboardDefaultsSeed();
        d.setClosingIn7Days(0);
        d.setMissingMandatoryDocs(0);
        d.setOverdueActions(0);
        d.setWinRatePercent(42);
        d.setDigestStatus("SUCCESS");
        d.setDigestLastRun("2026-08-05T08:00:00+05:30");
        return d;
    }

    public void apply(DashboardDefaultsSeed source) {
        if (source != null) {
            this.defaults = source;
        }
    }

    public long getClosingIn7Days() {
        return defaults.getClosingIn7Days();
    }

    public long getMissingMandatoryDocs() {
        return defaults.getMissingMandatoryDocs();
    }

    public long getOverdueActions() {
        return defaults.getOverdueActions();
    }

    public int getWinRatePercent() {
        return defaults.getWinRatePercent();
    }

    public String getDigestStatus() {
        return defaults.getDigestStatus();
    }

    public String getDigestLastRun() {
        return defaults.getDigestLastRun();
    }
}
