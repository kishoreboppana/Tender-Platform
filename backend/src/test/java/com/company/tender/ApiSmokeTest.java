package com.company.tender;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
    void customersEndpointReturnsList() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$.items[0].customerCode").exists())
                .andExpect(jsonPath("$.items[0].active").value(true));
    }

    @Test
    @Order(4)
    void tenderDetailEndpointReturnsItem() throws Exception {
        mockMvc.perform(get("/api/tenders/TND-2026-0012"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.tenderId").value("TND-2026-0012"))
                .andExpect(jsonPath("$.item.tenderReference").exists())
                .andExpect(jsonPath("$.item.customerCode").exists());
    }

    @Test
    @Order(5)
    void createCustomerEndpointPersistsRow() throws Exception {
        String customerCode = "CUS-TEST-" + System.currentTimeMillis();
        String body = "{"
                + "\"customerCode\":\"" + customerCode + "\","
                + "\"name\":\"Test Customer Org\","
                + "\"customerType\":\"PRIVATE\","
                + "\"contactEmail\":\"test@example.com\","
                + "\"active\":true"
                + "}";

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.item.customerCode").value(customerCode))
                .andExpect(jsonPath("$.item.name").value("Test Customer Org"))
                .andExpect(jsonPath("$.item.active").value(true));
    }

    @Test
    @Order(6)
    void tenderDocumentsEndpointSupportsUpload() throws Exception {
        MockMultipartFile pdf = new MockMultipartFile(
                "file",
                "authorization.pdf",
                "application/pdf",
                "%PDF-1.4\n% test document".getBytes("UTF-8"));

        org.springframework.test.web.servlet.MvcResult uploadResult = mockMvc.perform(
                        multipart("/api/tenders/TND-2026-0012/documents")
                                .file(pdf)
                                .param("docName", "OEM Authorization"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.item.docName").value("OEM Authorization"))
                .andExpect(jsonPath("$.item.approvalStatus").value("PENDING"))
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode uploadBody = mapper.readTree(uploadResult.getResponse().getContentAsString());
        long documentId = uploadBody.get("item").get("id").asLong();

        mockMvc.perform(post("/api/tenders/TND-2026-0012/documents/" + documentId + "/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"Missing authorized signatory\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.approvalStatus").value("REJECTED"))
                .andExpect(jsonPath("$.tenderHealth").value("RED"));

        mockMvc.perform(post("/api/tenders/TND-2026-0012/documents/" + documentId + "/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.approvalStatus").value("APPROVED"))
                .andExpect(jsonPath("$.item.rejectionReason").isEmpty());

        mockMvc.perform(post("/api/tenders/TND-2026-0012/documents/" + documentId + "/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"Stamp missing on page 2\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.approvalStatus").value("REJECTED"))
                .andExpect(jsonPath("$.item.rejectionReason").value("Stamp missing on page 2"));
    }

    @Test
    @Order(7)
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
