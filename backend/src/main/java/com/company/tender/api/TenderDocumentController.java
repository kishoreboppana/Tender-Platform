package com.company.tender.api;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.company.tender.api.dto.RejectDocumentRequest;
import com.company.tender.domain.Tender;
import com.company.tender.domain.TenderDocument;
import com.company.tender.service.TenderDocumentService;
import com.company.tender.service.TenderQueryService;

@RestController
public class TenderDocumentController {

    private final TenderDocumentService tenderDocumentService;
    private final TenderQueryService tenderQueryService;

    public TenderDocumentController(
            TenderDocumentService tenderDocumentService,
            TenderQueryService tenderQueryService) {
        this.tenderDocumentService = tenderDocumentService;
        this.tenderQueryService = tenderQueryService;
    }

    @GetMapping("/api/tenders/{tenderId}/documents")
    public Map<String, Object> list(@PathVariable String tenderId) {
        return TenderDocumentResponseMapper.listResponse(
                tenderDocumentService.listForTenderBusinessId(tenderId));
    }

    @PostMapping(value = "/api/tenders/{tenderId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> upload(
            @PathVariable String tenderId,
            @RequestParam("docName") String docName,
            @RequestParam("file") MultipartFile file) {
        TenderDocument document = tenderDocumentService.uploadAndSubmitForApproval(tenderId, docName, file);
        return documentActionResponse(tenderId, document);
    }

    @PostMapping("/api/tenders/{tenderId}/documents/{documentId}/approve")
    public Map<String, Object> approve(
            @PathVariable String tenderId,
            @PathVariable Long documentId) {
        TenderDocument document = tenderDocumentService.approve(tenderId, documentId);
        return documentActionResponse(tenderId, document);
    }

    @PostMapping("/api/tenders/{tenderId}/documents/{documentId}/reject")
    public Map<String, Object> reject(
            @PathVariable String tenderId,
            @PathVariable Long documentId,
            @Valid @RequestBody RejectDocumentRequest request) {
        TenderDocument document = tenderDocumentService.reject(tenderId, documentId, request.getReason());
        return documentActionResponse(tenderId, document);
    }

    @DeleteMapping("/api/tenders/{tenderId}/documents/{documentId}")
    public Map<String, Object> delete(
            @PathVariable String tenderId,
            @PathVariable Long documentId) {
        tenderDocumentService.delete(tenderId, documentId);
        return tenderHealthResponse(tenderId);
    }

    @GetMapping("/api/tenders/{tenderId}/documents/{documentId}/download")
    public ResponseEntity<Resource> download(
            @PathVariable String tenderId,
            @PathVariable Long documentId) {
        TenderDocument document = tenderDocumentService.getDocument(tenderId, documentId);
        Resource resource = tenderDocumentService.loadDocumentFile(tenderId, documentId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + document.getFileName() + "\"")
                .body(resource);
    }

    private Map<String, Object> documentActionResponse(String tenderId, TenderDocument document) {
        Map<String, Object> body = tenderHealthResponse(tenderId);
        body.put("item", TenderDocumentResponseMapper.toRow(document));
        return body;
    }

    private Map<String, Object> tenderHealthResponse(String tenderId) {
        Tender tender = tenderQueryService.getDemoTenderByBusinessId(tenderId);
        Map<String, Object> body = new HashMap<>();
        body.put("tenderHealth", tender.getHealth());
        body.put("tenderStatusComment", tender.getLatestStatusComment());
        return body;
    }
}
