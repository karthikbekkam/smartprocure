# SmartProcure – Enterprise Procurement & ERP System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

SmartProcure is a realistic, production-oriented enterprise procurement and vendor ERP system built using **Java 21**, **Spring Boot 3**, **Spring Security 6**, **MySQL / H2**, **Hibernate JPA**, **Thymeleaf**, and **Bootstrap 5**.

---

## 🏛️ System Architecture

SmartProcure follows a **Domain-Driven Design (DDD)** and **Layered Architecture**:

```
   [ Web Browser / Thymeleaf / REST Clients ]
                      │
                      ▼
   [ Spring Security 6 (RBAC Role-Based Authorization + JWT) ]
                      │
                      ▼
   [ MVC & REST Controllers (Validation, DTO Mappings, OpenAPI) ]
                      │
                      ▼
   [ Business Service Layer (Transactions, Workflows, Audit Log) ]
                      │
                      ▼
   [ Data Access Layer (Spring Data JPA / Hibernate) ]
                      │
                      ▼
   [ Relational Database (MySQL 8.0 / H2 Engine) ]
```

---

## 🔑 Key Enterprise Features

1. **Role-Based Navigation & Security**:
   - Distinct role-tailored workspaces for `ROLE_ADMIN`, `ROLE_PROCUREMENT_MANAGER`, `ROLE_FINANCE_MANAGER`, `ROLE_WAREHOUSE_MANAGER`, and `ROLE_VENDOR`.
   - Strict backend route protection with customized 403 Forbidden pages.

2. **Vendor Lifecycle & Qualification**:
   - Onboarding requests, tax ID verification, compliance approval workflow, rating scorecards.

3. **Product Catalog & Inventory**:
   - SKU management, reorder points, low-stock threshold alerts, multi-warehouse stock adjustments with audit trails.

4. **Purchase Requisitions & Orders**:
   - Departmental PR submission, manager approval routing, rejection with reason logging, single-click conversion to PO, status tracking (`ISSUED`, `CONFIRMED`, `RECEIVED`, `CANCELLED`).

5. **Invoices & 3-Way Matching**:
   - Electronic invoice submission against POs, 3-way matching validation (`MATCHED`, `PARTIALLY_MATCHED`, `MISMATCH`, `PENDING_REVIEW`).

6. **Payments & Disbursement Ledger**:
   - Payment eligibility validation, duplicate payment prevention, remaining balance tracking, multi-method disbursements (ACH, Wire, Check, Credit Card).

---

## 👥 Pre-Configured Test Accounts

| Role | Email | Password | Authorized Modules |
| :--- | :--- | :--- | :--- |
| **System Admin** | `admin@smartprocure.com` | `admin123` | All Modules |
| **Procurement Manager** | `procurement@smartprocure.com` | `admin123` | Dashboard, Vendors, Products, PRs, POs, Profile |
| **Finance Manager** | `finance@smartprocure.com` | `admin123` | Dashboard, Vendors, POs, Invoices, Payments, Profile |
| **Warehouse Manager** | `warehouse@smartprocure.com` | `admin123` | Dashboard, Inventory, Warehouses, POs, Profile |
| **Vendor** | `vendor@acme.com` | `admin123` | Dashboard, POs, Invoices, Profile |

---

## 🚀 Local Execution & Docker Setup

### Building the Project
```bash
mvn clean package -DskipTests
```

### Running Locally
```bash
java -jar target/smartprocure-1.0.0-SNAPSHOT.jar
```
Access the application at `http://localhost:8080/smartprocure`

### Docker Compose
```bash
docker-compose up --build
```
