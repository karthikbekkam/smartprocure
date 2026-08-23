package com.smartprocure.dto.response;

import com.smartprocure.domain.enums.VendorStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorResponseDTO {
    private Long id;
    private String companyName;
    private String taxId;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private VendorStatus status;
    private Double rating;
    private String paymentTerms;
    private LocalDateTime createdAt;
}
