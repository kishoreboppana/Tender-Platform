package com.company.tender.api;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.company.tender.api.dto.CreateTenderRequest;
import com.company.tender.domain.Tender;
import com.company.tender.service.TenderCommandService;
import com.company.tender.service.TenderQueryService;

@RestController
public class TenderController {

    private final TenderQueryService tenderQueryService;
    private final TenderCommandService tenderCommandService;

    public TenderController(
            TenderQueryService tenderQueryService,
            TenderCommandService tenderCommandService) {
        this.tenderQueryService = tenderQueryService;
        this.tenderCommandService = tenderCommandService;
    }

    @GetMapping("/api/tenders")
    public Map<String, Object> list() {
        return TenderResponseMapper.listResponse(tenderQueryService.listDemoTenders());
    }

    @GetMapping("/api/tenders/{tenderId}")
    public Map<String, Object> get(@PathVariable String tenderId) {
        Tender tender = tenderQueryService.getDemoTenderByBusinessId(tenderId);
        Map<String, Object> body = new HashMap<>();
        body.put("item", TenderResponseMapper.toDetail(tender));
        return body;
    }

    @PostMapping("/api/tenders")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@Valid @RequestBody CreateTenderRequest request) {
        Tender tender = tenderCommandService.createDemoTender(request);
        Map<String, Object> body = new HashMap<>();
        body.put("item", TenderResponseMapper.toRow(tender));
        return body;
    }
}
