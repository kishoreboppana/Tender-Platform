package com.company.tender.service;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.company.tender.config.DocumentStorageProperties;
import com.company.tender.domain.Tender;
import com.company.tender.domain.TenderDocument;
import com.company.tender.domain.TenderDocumentRepository;
import com.company.tender.domain.TenderRepository;

@Service
public class TenderDocumentService {

    private static final String DEMO_USER = "Demo User";

    private final TenderRepository tenderRepository;
    private final TenderDocumentRepository tenderDocumentRepository;
    private final TenderQueryService tenderQueryService;
    private final LocalDocumentStorageService storageService;
    private final TenderHealthService tenderHealthService;
    private final DocumentStorageProperties storageProperties;

    public TenderDocumentService(
            TenderRepository tenderRepository,
            TenderDocumentRepository tenderDocumentRepository,
            TenderQueryService tenderQueryService,
            LocalDocumentStorageService storageService,
            TenderHealthService tenderHealthService,
            DocumentStorageProperties storageProperties) {
        this.tenderRepository = tenderRepository;
        this.tenderDocumentRepository = tenderDocumentRepository;
        this.tenderQueryService = tenderQueryService;
        this.storageService = storageService;
        this.tenderHealthService = tenderHealthService;
        this.storageProperties = storageProperties;
    }

    @Transactional(readOnly = true)
    public List<TenderDocument> listForTenderBusinessId(String tenderBusinessId) {
        Tender tender = tenderQueryService.getDemoTenderByBusinessId(tenderBusinessId);
        return tenderDocumentRepository.findByTenantIdAndTender_IdOrderByUploadedAtDesc(
                TenderQueryService.DEMO_TENANT_ID, tender.getId());
    }

    @Transactional
    public TenderDocument uploadAndSubmitForApproval(String tenderBusinessId, String docName, MultipartFile file) {
        validateUpload(docName, file);

        Tender tender = tenderQueryService.getDemoTenderByBusinessId(tenderBusinessId);
        LocalDocumentStorageService.StoredDocument stored;
        try {
            stored = storageService.store(tenderBusinessId, file);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store document file", ex);
        }

        Instant now = Instant.now();
        TenderDocument document = new TenderDocument();
        document.setTenantId(TenderQueryService.DEMO_TENANT_ID);
        document.setTender(tender);
        document.setDocName(docName.trim());
        document.setFileName(stored.getOriginalFileName());
        document.setStoredPath(stored.getRelativePath());
        document.setFileSizeBytes(stored.getSizeBytes());
        document.setContentType("application/pdf");
        document.setApprovalStatus("PENDING");
        document.setUploadedBy(DEMO_USER);
        document.setUploadedAt(now);
        document.setSubmittedAt(now);

        TenderDocument saved = tenderDocumentRepository.save(document);
        refreshTenderHealth(tender);
        return saved;
    }

    @Transactional(readOnly = true)
    public Resource loadDocumentFile(String tenderBusinessId, Long documentId) {
        Tender tender = tenderQueryService.getDemoTenderByBusinessId(tenderBusinessId);
        TenderDocument document = tenderDocumentRepository
                .findByIdAndTenantIdAndTender_Id(documentId, TenderQueryService.DEMO_TENANT_ID, tender.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Document not found"));

        try {
            if (!Files.exists(storageService.resolve(document.getStoredPath()))) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document file missing on disk");
            }
        } catch (SecurityException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document file missing on disk");
        }

        return new FileSystemResource(storageService.resolve(document.getStoredPath()).toFile());
    }

    @Transactional(readOnly = true)
    public TenderDocument getDocument(String tenderBusinessId, Long documentId) {
        return findDocument(tenderBusinessId, documentId);
    }

    @Transactional
    public TenderDocument approve(String tenderBusinessId, Long documentId) {
        Tender tender = tenderQueryService.getDemoTenderByBusinessId(tenderBusinessId);
        TenderDocument document = findDocument(tenderBusinessId, documentId);
        if ("APPROVED".equals(document.getApprovalStatus())) {
            return document;
        }

        Instant now = Instant.now();
        document.setApprovalStatus("APPROVED");
        document.setRejectionReason(null);
        document.setReviewedAt(now);
        document.setReviewedBy(DEMO_USER);

        TenderDocument saved = tenderDocumentRepository.save(document);
        refreshTenderHealth(tender);
        return saved;
    }

    @Transactional
    public TenderDocument reject(String tenderBusinessId, Long documentId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Rejection reason is required");
        }

        Tender tender = tenderQueryService.getDemoTenderByBusinessId(tenderBusinessId);
        TenderDocument document = findDocument(tenderBusinessId, documentId);

        Instant now = Instant.now();
        document.setApprovalStatus("REJECTED");
        document.setRejectionReason(reason.trim());
        document.setReviewedAt(now);
        document.setReviewedBy(DEMO_USER);

        TenderDocument saved = tenderDocumentRepository.save(document);
        refreshTenderHealth(tender);
        return saved;
    }

    @Transactional
    public void delete(String tenderBusinessId, Long documentId) {
        Tender tender = tenderQueryService.getDemoTenderByBusinessId(tenderBusinessId);
        TenderDocument document = findDocument(tenderBusinessId, documentId);

        try {
            Files.deleteIfExists(storageService.resolve(document.getStoredPath()));
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to delete document file", ex);
        }

        tenderDocumentRepository.delete(document);
        refreshTenderHealth(tender);
    }

    private TenderDocument findDocument(String tenderBusinessId, Long documentId) {
        Tender tender = tenderQueryService.getDemoTenderByBusinessId(tenderBusinessId);
        return tenderDocumentRepository
                .findByIdAndTenantIdAndTender_Id(documentId, TenderQueryService.DEMO_TENANT_ID, tender.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Document not found"));
    }

    private void refreshTenderHealth(Tender tender) {
        List<TenderDocument> documents = tenderDocumentRepository
                .findByTenantIdAndTender_IdOrderByUploadedAtDesc(
                        TenderQueryService.DEMO_TENANT_ID, tender.getId());
        tenderHealthService.applyDocumentHealth(tender, documents);
        tenderRepository.save(tender);
    }

    private void validateUpload(String docName, MultipartFile file) {
        if (docName == null || docName.trim().isEmpty()) {
            throw new IllegalArgumentException("Document name is required");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("PDF file is required");
        }
        if (file.getSize() > storageProperties.getMaxSizeBytes()) {
            throw new IllegalArgumentException("File exceeds maximum size of 5 MB");
        }

        String originalName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase();
        if (!originalName.endsWith(".pdf") && !"application/pdf".equals(contentType)) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        try (java.io.InputStream input = file.getInputStream()) {
            byte[] header = new byte[4];
            int read = input.read(header);
            if (read < 4 || header[0] != '%' || header[1] != 'P' || header[2] != 'D' || header[3] != 'F') {
                throw new IllegalArgumentException("Only PDF files are allowed");
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to read uploaded file");
        }
    }
}
