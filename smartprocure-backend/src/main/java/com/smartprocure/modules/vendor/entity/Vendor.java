package com.smartprocure.modules.vendor.entity;

import com.smartprocure.core.entity.BaseEntity;
import com.smartprocure.modules.auth.entity.User;
import com.smartprocure.modules.vendor.enums.VendorStatus;
import jakarta.persistence.*;
import lombok.*;

/**
 * Enterprise Vendor Entity representing registered suppliers, contractors, and partner organizations.
 *
 * @author Principal Java Architect
 */
@Entity
@Table(name = "vendors", indexes = {
        @Index(name = "idx_vendor_code", columnList = "vendor_code", unique = true),
        @Index(name = "idx_vendor_tax_id", columnList = "tax_id", unique = true),
        @Index(name = "idx_vendor_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vendor extends BaseEntity {

    @Column(name = "vendor_code", nullable = false, unique = true, length = 30)
    private String vendorCode;

    @Column(name = "company_name", nullable = false, length = 150)
    private String companyName;

    @Column(name = "contact_person_name", length = 100)
    private String contactPersonName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "tax_id", nullable = false, unique = true, length = 50)
    private String taxId;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "bank_account_number", length = 50)
    private String bankAccountNumber;

    @Column(name = "swift_bic", length = 20)
    private String swiftBic;

    @Column(name = "rating")
    @Builder.Default
    private Double rating = 5.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private VendorStatus status = VendorStatus.PENDING_APPROVAL;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
