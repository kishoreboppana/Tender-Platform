package com.company.tender.api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class RejectDocumentRequest {

    @NotBlank
    @Size(max = 2000)
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
