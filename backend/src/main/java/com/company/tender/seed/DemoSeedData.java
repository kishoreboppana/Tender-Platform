package com.company.tender.seed;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoSeedData {

    private TenantSeed tenant = new TenantSeed();
    private List<AppUserSeed> appUsers = new ArrayList<>();
    private List<CustomerSeed> customers = new ArrayList<>();
    private List<BusinessSequenceSeed> businessSequences = new ArrayList<>();
    private List<TenderSeed> tenders = new ArrayList<>();
    private List<AuditEventSeed> auditEvents = new ArrayList<>();
    private DashboardDefaultsSeed dashboardDefaults = new DashboardDefaultsSeed();

    public TenantSeed getTenant() {
        return tenant;
    }

    public void setTenant(TenantSeed tenant) {
        this.tenant = tenant;
    }

    public List<AppUserSeed> getAppUsers() {
        return appUsers;
    }

    public void setAppUsers(List<AppUserSeed> appUsers) {
        this.appUsers = appUsers;
    }

    public List<CustomerSeed> getCustomers() {
        return customers;
    }

    public void setCustomers(List<CustomerSeed> customers) {
        this.customers = customers;
    }

    public List<BusinessSequenceSeed> getBusinessSequences() {
        return businessSequences;
    }

    public void setBusinessSequences(List<BusinessSequenceSeed> businessSequences) {
        this.businessSequences = businessSequences;
    }

    public List<TenderSeed> getTenders() {
        return tenders;
    }

    public void setTenders(List<TenderSeed> tenders) {
        this.tenders = tenders;
    }

    public List<AuditEventSeed> getAuditEvents() {
        return auditEvents;
    }

    public void setAuditEvents(List<AuditEventSeed> auditEvents) {
        this.auditEvents = auditEvents;
    }

    public DashboardDefaultsSeed getDashboardDefaults() {
        return dashboardDefaults;
    }

    public void setDashboardDefaults(DashboardDefaultsSeed dashboardDefaults) {
        this.dashboardDefaults = dashboardDefaults;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TenantSeed {
        private String id;
        private String code;
        private String name;
        private String status;
        private String timezone;
        private String databaseMode;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public String getDatabaseMode() {
            return databaseMode;
        }

        public void setDatabaseMode(String databaseMode) {
            this.databaseMode = databaseMode;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AppUserSeed {
        private String email;
        private String displayName;
        private boolean active = true;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CustomerSeed {
        private String customerCode;
        private String name;
        private String customerType;
        private String contactEmail;
        private boolean active = true;

        public String getCustomerCode() {
            return customerCode;
        }

        public void setCustomerCode(String customerCode) {
            this.customerCode = customerCode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCustomerType() {
            return customerType;
        }

        public void setCustomerType(String customerType) {
            this.customerType = customerType;
        }

        public String getContactEmail() {
            return contactEmail;
        }

        public void setContactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BusinessSequenceSeed {
        private String sequenceName;
        private int sequenceYear;
        private long currentValue;

        public String getSequenceName() {
            return sequenceName;
        }

        public void setSequenceName(String sequenceName) {
            this.sequenceName = sequenceName;
        }

        public int getSequenceYear() {
            return sequenceYear;
        }

        public void setSequenceYear(int sequenceYear) {
            this.sequenceYear = sequenceYear;
        }

        public long getCurrentValue() {
            return currentValue;
        }

        public void setCurrentValue(long currentValue) {
            this.currentValue = currentValue;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TenderSeed {
        private String tenderId;
        private String name;
        private String customerCode;
        private String tenderReference;
        private String tenderType;
        private Double estimatedValue;
        private String currency;
        private String closingAt;
        private String submissionMode;
        private String tenderOwnerName;
        private String status;
        private String priority;
        private String approvalStatus;
        private String health;
        private int completionPercent;
        private String latestStatusComment;

        public String getTenderId() {
            return tenderId;
        }

        public void setTenderId(String tenderId) {
            this.tenderId = tenderId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCustomerCode() {
            return customerCode;
        }

        public void setCustomerCode(String customerCode) {
            this.customerCode = customerCode;
        }

        public String getTenderReference() {
            return tenderReference;
        }

        public void setTenderReference(String tenderReference) {
            this.tenderReference = tenderReference;
        }

        public String getTenderType() {
            return tenderType;
        }

        public void setTenderType(String tenderType) {
            this.tenderType = tenderType;
        }

        public Double getEstimatedValue() {
            return estimatedValue;
        }

        public void setEstimatedValue(Double estimatedValue) {
            this.estimatedValue = estimatedValue;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getClosingAt() {
            return closingAt;
        }

        public void setClosingAt(String closingAt) {
            this.closingAt = closingAt;
        }

        public String getSubmissionMode() {
            return submissionMode;
        }

        public void setSubmissionMode(String submissionMode) {
            this.submissionMode = submissionMode;
        }

        public String getTenderOwnerName() {
            return tenderOwnerName;
        }

        public void setTenderOwnerName(String tenderOwnerName) {
            this.tenderOwnerName = tenderOwnerName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public String getApprovalStatus() {
            return approvalStatus;
        }

        public void setApprovalStatus(String approvalStatus) {
            this.approvalStatus = approvalStatus;
        }

        public String getHealth() {
            return health;
        }

        public void setHealth(String health) {
            this.health = health;
        }

        public int getCompletionPercent() {
            return completionPercent;
        }

        public void setCompletionPercent(int completionPercent) {
            this.completionPercent = completionPercent;
        }

        public String getLatestStatusComment() {
            return latestStatusComment;
        }

        public void setLatestStatusComment(String latestStatusComment) {
            this.latestStatusComment = latestStatusComment;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuditEventSeed {
        private String eventType;
        private String entityType;
        private String entityId;
        private String actor;
        private String detail;

        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }

        public String getEntityType() {
            return entityType;
        }

        public void setEntityType(String entityType) {
            this.entityType = entityType;
        }

        public String getEntityId() {
            return entityId;
        }

        public void setEntityId(String entityId) {
            this.entityId = entityId;
        }

        public String getActor() {
            return actor;
        }

        public void setActor(String actor) {
            this.actor = actor;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DashboardDefaultsSeed {
        private long closingIn7Days = 0;
        private long missingMandatoryDocs = 0;
        private long overdueActions = 0;
        private int winRatePercent = 0;
        private String digestStatus = "SUCCESS";
        private String digestLastRun;

        public long getClosingIn7Days() {
            return closingIn7Days;
        }

        public void setClosingIn7Days(long closingIn7Days) {
            this.closingIn7Days = closingIn7Days;
        }

        public long getMissingMandatoryDocs() {
            return missingMandatoryDocs;
        }

        public void setMissingMandatoryDocs(long missingMandatoryDocs) {
            this.missingMandatoryDocs = missingMandatoryDocs;
        }

        public long getOverdueActions() {
            return overdueActions;
        }

        public void setOverdueActions(long overdueActions) {
            this.overdueActions = overdueActions;
        }

        public int getWinRatePercent() {
            return winRatePercent;
        }

        public void setWinRatePercent(int winRatePercent) {
            this.winRatePercent = winRatePercent;
        }

        public String getDigestStatus() {
            return digestStatus;
        }

        public void setDigestStatus(String digestStatus) {
            this.digestStatus = digestStatus;
        }

        public String getDigestLastRun() {
            return digestLastRun;
        }

        public void setDigestLastRun(String digestLastRun) {
            this.digestLastRun = digestLastRun;
        }
    }
}
