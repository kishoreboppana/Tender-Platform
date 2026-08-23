package com.company.tender.seed;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.company.tender.config.SeedProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Always applies dashboard KPI defaults from the static seed JSON so APIs and UI stay in sync with the file.
 */
@Component
public class SeedDashboardBootstrap {

    private static final Logger log = LoggerFactory.getLogger(SeedDashboardBootstrap.class);

    private final SeedProperties seedProperties;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final SeedDashboardDefaults seedDashboardDefaults;

    public SeedDashboardBootstrap(
            SeedProperties seedProperties,
            ResourceLoader resourceLoader,
            ObjectMapper objectMapper,
            SeedDashboardDefaults seedDashboardDefaults) {
        this.seedProperties = seedProperties;
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
        this.seedDashboardDefaults = seedDashboardDefaults;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void applyDashboardDefaults() throws IOException {
        Resource resource = resourceLoader.getResource(seedProperties.getDataLocation());
        if (!resource.exists()) {
            log.warn("Seed file not found: {}", seedProperties.getDataLocation());
            return;
        }
        try (InputStream in = resource.getInputStream()) {
            DemoSeedData data = objectMapper.readValue(in, DemoSeedData.class);
            seedDashboardDefaults.apply(data.getDashboardDefaults());
            log.info("Dashboard defaults loaded from {}", seedProperties.getDataLocation());
        }
    }
}
