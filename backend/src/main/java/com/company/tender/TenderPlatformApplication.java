package com.company.tender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.company.tender.config.DocumentStorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(DocumentStorageProperties.class)
public class TenderPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenderPlatformApplication.class, args);
    }
}
