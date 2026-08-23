# SmartProcure - Security & Authorization Architecture

## Security Layer Blueprint

SmartProcure implements a defense-in-depth security model using Spring Security 6, JJWT (Java JWT Library), BCrypt Password Hashing, and Method-Level `@PreAuthorize` Permission Evaluation.

```mermaid
sequenceDiagram
    autonumber
    actor User as Client Browser
    participant AuthFilter as JwtAuthenticationFilter
    participant AuthMgr as AuthenticationManager
    participant TokenProvider as JwtTokenProvider
    participant DB as MySQL Database

    User->>AuthFilter: POST /api/v1/auth/login {email, password}
    AuthFilter->>AuthMgr: authenticate(UsernamePasswordAuthenticationToken)
    AuthMgr->>DB: Query User & BCrypt Hash
    DB-->>AuthMgr: Validated Principal
    AuthMgr->>TokenProvider: generateToken(Authentication)
    TokenProvider-->>User: JWT Bearer Access Token & HttpOnly Cookie
```

## Role-Based Access Control (RBAC) Matrix

| Role | Vendor Mgmt | Product Catalog | Stock Control | PR & PO Approval | Financial Matching & Payment |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **ADMINISTRATOR** | Full | Full | Full | Full | Full |
| **PROCUREMENT_MANAGER** | Approve/Reject | Modify | Read | Full Approval | Read |
| **WAREHOUSE_MANAGER** | Read | Read | Adjust / Transfer | Read | Read |
| **FINANCE_MANAGER** | Read | Read | Read | Read | Full Payment |
| **VENDOR** | Self-Manage | Self Catalog | N/A | View Assigned POs | Submit Invoices |
