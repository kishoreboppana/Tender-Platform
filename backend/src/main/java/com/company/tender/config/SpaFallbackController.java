package com.company.tender.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Forwards deep SPA routes to {@code index.html} when the UI is bundled into the backend.
 */
@Controller
public class SpaFallbackController {

    @GetMapping("/tenders/{tenderId}")
    public String tenderDetail() {
        return "forward:/index.html";
    }
}
