package com.smartprocure.web.rest;

import com.smartprocure.dto.request.PaymentCreateDTO;
import com.smartprocure.dto.response.ApiResponse;
import com.smartprocure.dto.response.PaymentResponseDTO;
import com.smartprocure.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "Endpoints for vendor disbursements, payment processing, and ledger records")
public class PaymentRestController {

    private final PaymentService paymentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_MANAGER', 'VENDOR')")
    @Operation(summary = "Get All Payments", description = "Retrieve list of all enterprise financial payments and disbursements")
    public ResponseEntity<ApiResponse> getAllPayments() {
        List<PaymentResponseDTO> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(ApiResponse.ok("Payments retrieved successfully", payments));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_MANAGER', 'VENDOR')")
    @Operation(summary = "Get Payment by ID", description = "Retrieve detailed information of a specific payment transaction")
    public ResponseEntity<ApiResponse> getPaymentById(@PathVariable Long id) {
        PaymentResponseDTO payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.ok("Payment details retrieved", payment));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_MANAGER', 'PROCUREMENT_MANAGER')")
    @Operation(summary = "Process Payment / Record Disbursement", description = "Authorize disbursement and update invoice status")
    public ResponseEntity<ApiResponse> processPayment(
            @Valid @RequestBody PaymentCreateDTO paymentCreateDTO,
            Authentication authentication
    ) {
        String username = authentication != null ? authentication.getName() : null;
        PaymentResponseDTO payment = paymentService.processPayment(paymentCreateDTO, username);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Disbursement of $" + payment.getAmount() + " processed successfully for Invoice " + payment.getInvoiceNumber(), payment));
    }
}
