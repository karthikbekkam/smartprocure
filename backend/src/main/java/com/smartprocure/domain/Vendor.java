package com.smartprocure.domain;

import com.smartprocure.common.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vendors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vendor extends AuditableEntity {

    @Column(name = "company_name", nullable = false, unique = true, length = 150)
    private String companyName;

    @Column(name = "tax_id", nullable = false, unique = true, length = 50)
    private String taxId;

    @Column(name = "contact_email", nullable = false, length = 150)
    private String contactEmail;

    @Column(name = "contact_phone", nullable = false, length = 30)
    private String contactPhone;

    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private String status = "REGISTERED"; // REGISTERED, UNDER_REVIEW, APPROVED, REJECTED, ACTIVE, INACTIVE

    @Column(name = "rating")
    @Builder.Default
    private Double rating = 0.0;

    @Column(name = "payment_terms", length = 100)
    @Builder.Default
    private String paymentTerms = "NET_30";

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "business_license_number", length = 100)
    private String businessLicenseNumber;

    @Column(name = "bank_account_details", length = 255)
    private String bankAccountDetails;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}
