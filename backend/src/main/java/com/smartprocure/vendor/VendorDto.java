package com.smartprocure.vendor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorDto {

    private Long id;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Tax ID is required")
    private String taxId;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Invalid contact email format")
    private String contactEmail;

    @NotBlank(message = "Contact phone is required")
    private String contactPhone;

    @NotBlank(message = "Address is required")
    private String address;

    private String status;
    private Double rating;
    private String paymentTerms;
    private String rejectionReason;
    private String businessLicenseNumber;
    private String bankAccountDetails;
}
