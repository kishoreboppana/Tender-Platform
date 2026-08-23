package com.company.tender;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    void healthEndpointReturnsUp() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @Order(2)
    void tendersEndpointReturnsList() throws Exception {
        mockMvc.perform(get("/api/tenders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)));
    }

    @Test
    @Order(3)
    void createTenderEndpointPersistsRow() throws Exception {
        String body = "{"
                + "\"name\":\"Test Pipeline Lot\","
                + "\"customerId\":1,"
                + "\"tenderReference\":\"TEST/2026/001\","
                + "\"tenderType\":\"OPEN\","
                + "\"estimatedValue\":1000000,"
                + "\"currency\":\"INR\","
                + "\"closingAt\":\"2026-09-01T12:00:00Z\","
                + "\"tenderOwnerName\":\"Test Owner\","
                + "\"priority\":\"HIGH\","
                + "\"latestStatusComment\":\"Created via API test\","
                + "\"completionPercent\":10"
                + "}";

        mockMvc.perform(post("/api/tenders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.item.tenderId").exists())
                .andExpect(jsonPath("$.item.name").value("Test Pipeline Lot"))
                .andExpect(jsonPath("$.item.stage").value("DRAFT"));
    }
}
