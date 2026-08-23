package com.smartprocure.modules.vendor.dto;

import com.smartprocure.modules.vendor.enums.VendorStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Enterprise Data Transfer Object (DTO) for Vendor Master Data operations.
 *
 * @author Principal Java Architect
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorDTO {

    private Long id;

    private String vendorCode;

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 150, message = "Company name must be between 2 and 150 characters")
    private String companyName;

    @NotBlank(message = "Contact person name is required")
    private String contactPersonName;

    @NotBlank(message = "Vendor email is required")
    @Email(message = "Invalid vendor email format")
    private String email;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;

    @NotBlank(message = "Tax ID / VAT registration number is required")
    private String taxId;

    private String address;
    private String city;
    private String country;
    private String postalCode;

    private String bankName;
    private String bankAccountNumber;
    private String swiftBic;

    private Double rating;
    private VendorStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
