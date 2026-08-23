package com.smartprocure;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

/**
 * SmartProcure – Enterprise Vendor & Procurement Management System
 * Core Spring Boot Main Bootstrap Class
 *
 * @author Principal Java Architect
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class SmartProcureApplication {

    /**
     * Set JVM default timezone to UTC to enforce consistent datetime persistence across multi-region deployments.
     */
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        SpringApplication.run(SmartProcureApplication.class, args);
    }
}
