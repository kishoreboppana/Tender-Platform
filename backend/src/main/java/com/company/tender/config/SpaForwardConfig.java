package com.company.tender.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Serve React SPA routes from {@code classpath:/static/index.html} when the UI is
 * bundled into the backend JAR (Maven copies {@code frontend/dist} at build time).
 */
@Configuration
public class SpaForwardConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("forward:/index.html");
        registry.addViewController("/dashboard").setViewName("forward:/index.html");
        registry.addViewController("/tenders").setViewName("forward:/index.html");
        registry.addViewController("/tenders/new").setViewName("forward:/index.html");
    }
}
