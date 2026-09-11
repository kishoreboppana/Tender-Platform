package com.company.tender.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private java.util.UUID tenantId;

    @Column(name = "customer_code", nullable = false, length = 30)
    private String customerCode;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "customer_type", nullable = false, length = 30)
    private String customerType;

    @Column(name = "contact_email", length = 320)
    private String contactEmail;

    @Column(nullable = false)
    private boolean active;

    @Version
    private Long version;

    public Long getId() {
        return id;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public String getName() {
        return name;
    }

    public String getCustomerType() {
        return customerType;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public boolean isActive() {
        return active;
    }
}
