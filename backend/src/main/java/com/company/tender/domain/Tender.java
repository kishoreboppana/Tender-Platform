package com.company.tender.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "tender")
public class Tender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "public_id", nullable = false)
    private UUID publicId;

    @Column(name = "tender_id", nullable = false, length = 30)
    private String tenderId;

    @Column(nullable = false, length = 300)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "tender_reference", nullable = false, length = 150)
    private String tenderReference;

    @Column(name = "tender_type", nullable = false, length = 30)
    private String tenderType;

    @Column(name = "estimated_value")
    private BigDecimal estimatedValue;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "submission_mode", nullable = false, length = 30)
    private String submissionMode;

    @Column(nullable = false, length = 40)
    private String status;

    @Column(nullable = false, length = 20)
    private String priority;

    @Column(name = "approval_status", nullable = false, length = 20)
    private String approvalStatus;

    @Column(nullable = false, length = 10)
    private String health;

    @Column(name = "completion_percent", nullable = false)
    private short completionPercent;

    @Column(name = "tender_owner_name", nullable = false, length = 200)
    private String tenderOwnerName;

    @Column(name = "closing_at", nullable = false)
    private Instant closingAt;

    @Column(name = "latest_status_comment")
    private String latestStatusComment;

    @Version
    private Long version;

    public Long getId() {
        return id;
    }

    public String getTenderId() {
        return tenderId;
    }

    public String getName() {
        return name;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public String getHealth() {
        return health;
    }

    public short getCompletionPercent() {
        return completionPercent;
    }

    public String getTenderOwnerName() {
        return tenderOwnerName;
    }

    public Instant getClosingAt() {
        return closingAt;
    }

    public String getTenderReference() {
        return tenderReference;
    }

    public String getTenderType() {
        return tenderType;
    }

    public BigDecimal getEstimatedValue() {
        return estimatedValue;
    }

    public String getCurrency() {
        return currency;
    }

    public String getSubmissionMode() {
        return submissionMode;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public String getLatestStatusComment() {
        return latestStatusComment;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
    }

    public void setTenderId(String tenderId) {
        this.tenderId = tenderId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setTenderReference(String tenderReference) {
        this.tenderReference = tenderReference;
    }

    public void setTenderType(String tenderType) {
        this.tenderType = tenderType;
    }

    public void setEstimatedValue(BigDecimal estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setSubmissionMode(String submissionMode) {
        this.submissionMode = submissionMode;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public void setCompletionPercent(short completionPercent) {
        this.completionPercent = completionPercent;
    }

    public void setTenderOwnerName(String tenderOwnerName) {
        this.tenderOwnerName = tenderOwnerName;
    }

    public void setClosingAt(Instant closingAt) {
        this.closingAt = closingAt;
    }

    public void setLatestStatusComment(String latestStatusComment) {
        this.latestStatusComment = latestStatusComment;
    }
}
