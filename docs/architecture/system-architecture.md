# SmartProcure - System Architecture Specification

## Executive Architecture Summary

SmartProcure is an enterprise-grade Procurement & Vendor Management System designed following Domain-Driven Design (DDD), Layered Micro-Monolith Architecture, and Zero-Trust Security principles.

```
+-----------------------------------------------------------------------+
|                         PRESENTATION LAYER                            |
|    Thymeleaf Server-Side Rendered Views & ES6 Bootstrap UI Clients    |
+-----------------------------------------------------------------------+
                                    |
                                    v
+-----------------------------------------------------------------------+
|                          SECURITY LAYER                               |
|   Spring Security 6 (Stateless JWT / HttpOnly Cookies + CSRF Filter)  |
+-----------------------------------------------------------------------+
                                    |
                                    v
+-----------------------------------------------------------------------+
|                         APPLICATION LAYER                             |
|          REST Controllers & DTO Mapping (Jakarta Bean Validation)     |
+-----------------------------------------------------------------------+
                                    |
                                    v
+-----------------------------------------------------------------------+
|                           DOMAIN LAYER                                |
|        Business Services, State Machines & Transaction Boundaries     |
+-----------------------------------------------------------------------+
                                    |
                                    v
+-----------------------------------------------------------------------+
|                       PERSISTENCE LAYER                               |
|        Spring Data JPA / Hibernate 6 ORM with Optimistic Locking      |
+-----------------------------------------------------------------------+
                                    |
                                    v
+-----------------------------------------------------------------------+
|                       DATABASE STORAGE LAYER                          |
|         MySQL 8.0 Relational Database Engine (3NF Normalized)         |
+-----------------------------------------------------------------------+
```

## Architectural Tenets

1. **Domain-Driven Organization**: Backend code is organized around domain bounded contexts (`authentication`, `user`, `role`, `vendor`, `product`, `category`, `warehouse`, `inventory`, `purchase`, `invoice`, `payment`, `dashboard`).
2. **Stateless Security**: RESTful APIs utilize short-lived JWT access tokens and secure refresh token evaluation. Browser sessions utilize HttpOnly cookies.
3. **Optimistic Locking**: Inventory allocations and PO state changes use JPA `@Version` concurrency controls to prevent race conditions during high-volume operations.
4. **Financial Accuracy**: All monetary calculations utilize `java.math.BigDecimal` (precision 12, scale 2).
