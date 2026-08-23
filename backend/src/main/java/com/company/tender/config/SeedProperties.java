package com.company.tender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tender.seed")
public class SeedProperties {

    /**
     * Load demo rows from classpath seed file on startup (dev profile).
     */
    private boolean enabled = false;

    /**
     * Classpath location of JSON seed file.
     */
    private String dataLocation = "classpath:db/seed/demo-data.json";

    /**
     * Re-apply seed file even when demo tenant already exists (idempotent inserts).
     */
    private boolean force = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDataLocation() {
        return dataLocation;
    }

    public void setDataLocation(String dataLocation) {
        this.dataLocation = dataLocation;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }
}
