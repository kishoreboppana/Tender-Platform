package com.company.tender.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.company.tender.domain.Tender;
import com.company.tender.domain.TenderDocument;

@Service
public class TenderHealthService {

    public void applyDocumentHealth(Tender tender, List<TenderDocument> documents) {
        if (documents.isEmpty()) {
            tender.setHealth("RED");
            tender.setLatestStatusComment("Mandatory documents missing");
            return;
        }

        boolean hasRejected = documents.stream()
                .anyMatch(doc -> "REJECTED".equals(doc.getApprovalStatus()));
        if (hasRejected) {
            String rejectionDetail = documents.stream()
                    .filter(doc -> "REJECTED".equals(doc.getApprovalStatus()))
                    .map(TenderDocument::getRejectionReason)
                    .filter(reason -> reason != null && !reason.trim().isEmpty())
                    .findFirst()
                    .orElse(null);
            tender.setHealth("RED");
            tender.setLatestStatusComment(rejectionDetail == null
                    ? "One or more documents rejected"
                    : "Document rejected: " + rejectionDetail.trim());
            return;
        }

        boolean hasPending = documents.stream()
                .anyMatch(doc -> "PENDING".equals(doc.getApprovalStatus()));
        if (hasPending) {
            tender.setHealth("AMBER");
            tender.setLatestStatusComment("Documents pending approval");
            return;
        }

        tender.setHealth("GREEN");
        tender.setLatestStatusComment("All documents approved");
    }
}
