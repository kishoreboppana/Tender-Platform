package com.company.tender.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.company.tender.domain.TenderDocument;

final class TenderDocumentResponseMapper {

    private TenderDocumentResponseMapper() {
    }

    static Map<String, Object> toRow(TenderDocument document) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", document.getId());
        row.put("docName", document.getDocName());
        row.put("fileName", document.getFileName());
        row.put("fileSizeBytes", document.getFileSizeBytes());
        row.put("approvalStatus", document.getApprovalStatus());
        row.put("uploadedBy", document.getUploadedBy());
        row.put("uploadedAt", document.getUploadedAt().toString());
        row.put("submittedAt", document.getSubmittedAt() == null
                ? null
                : document.getSubmittedAt().toString());
        row.put("rejectionReason", document.getRejectionReason());
        row.put("reviewedAt", document.getReviewedAt() == null
                ? null
                : document.getReviewedAt().toString());
        row.put("reviewedBy", document.getReviewedBy());
        return row;
    }

    static Map<String, Object> listResponse(List<TenderDocument> documents) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (TenderDocument document : documents) {
            items.add(toRow(document));
        }
        Map<String, Object> body = new HashMap<>();
        body.put("items", items);
        body.put("total", items.size());
        return body;
    }
}
