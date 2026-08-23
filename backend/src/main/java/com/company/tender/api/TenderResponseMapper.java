package com.company.tender.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.company.tender.domain.Tender;

final class TenderResponseMapper {

    private TenderResponseMapper() {
    }

    static Map<String, Object> listResponse(List<Tender> tenders) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (Tender t : tenders) {
            items.add(toRow(t));
        }
        Map<String, Object> body = new HashMap<>();
        body.put("items", items);
        body.put("total", items.size());
        return body;
    }

    static Map<String, Object> toRow(Tender t) {
        Map<String, Object> row = new HashMap<>();
        row.put("tenderId", t.getTenderId());
        row.put("customer", t.getCustomer().getName());
        row.put("name", t.getName());
        row.put("owner", t.getTenderOwnerName());
        row.put("stage", t.getStatus());
        row.put("priority", t.getPriority());
        row.put("closingAt", t.getClosingAt().toString());
        row.put("health", t.getHealth());
        row.put("completionPercent", t.getCompletionPercent());
        return row;
    }
}
