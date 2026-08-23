package com.company.tender.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateTenderRequest {

    @NotBlank
    @Size(max = 300)
    private String name;

    @NotNull
    private Long customerId;

    @NotBlank
    @Size(max = 150)
    private String tenderReference;

    @NotBlank
    @Size(max = 30)
    private String tenderType;

    @DecimalMin(value = "0", inclusive = false)
    private BigDecimal estimatedValue;

    @Size(max = 3)
    private String currency;

    @NotNull
    private Instant closingAt;

    @NotBlank
    @Size(max = 200)
    private String tenderOwnerName;

    @NotBlank
    @Size(max = 20)
    private String priority;

    @Size(max = 2000)
    private String latestStatusComment;

    @Min(0)
    @Max(100)
    private Short completionPercent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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

    public BigDecimal getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(BigDecimal estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Instant getClosingAt() {
        return closingAt;
    }

    public void setClosingAt(Instant closingAt) {
        this.closingAt = closingAt;
    }

    public String getTenderOwnerName() {
        return tenderOwnerName;
    }

    public void setTenderOwnerName(String tenderOwnerName) {
        this.tenderOwnerName = tenderOwnerName;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getLatestStatusComment() {
        return latestStatusComment;
    }

    public void setLatestStatusComment(String latestStatusComment) {
        this.latestStatusComment = latestStatusComment;
    }

    public Short getCompletionPercent() {
        return completionPercent;
    }

    public void setCompletionPercent(Short completionPercent) {
        this.completionPercent = completionPercent;
    }
}
