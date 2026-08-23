package com.smartprocure.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendorCreateDTO {

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Tax ID / EIN is required")
    private String taxId;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Invalid email format")
    private String contactEmail;

    @NotBlank(message = "Contact phone is required")
    private String contactPhone;

    @NotBlank(message = "Address is required")
    private String address;

    private String paymentTerms;
}
