package com.smartprocure.web.rest;

import com.smartprocure.domain.enums.InvoiceStatus;
import com.smartprocure.dto.request.InvoiceCreateDTO;
import com.smartprocure.dto.response.ApiResponse;
import com.smartprocure.dto.response.InvoiceResponseDTO;
import com.smartprocure.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoice Management", description = "Endpoints for managing billing, 3-way invoice matching, and approval workflows")
public class InvoiceRestController {

    private final InvoiceService invoiceService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_MANAGER', 'VENDOR')")
    @Operation(summary = "Get All Invoices", description = "Retrieve list of all enterprise invoices")
    public ResponseEntity<ApiResponse> getAllInvoices() {
        List<InvoiceResponseDTO> invoices = invoiceService.getAllInvoices();
        return ResponseEntity.ok(ApiResponse.ok("Invoices retrieved successfully", invoices));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_MANAGER', 'VENDOR')")
    @Operation(summary = "Get Invoice by ID", description = "Retrieve detailed information of a specific invoice")
    public ResponseEntity<ApiResponse> getInvoiceById(@PathVariable Long id) {
        InvoiceResponseDTO invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(ApiResponse.ok("Invoice details retrieved", invoice));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_MANAGER', 'VENDOR')")
    @Operation(summary = "Create Invoice", description = "Create a new invoice for a confirmed Purchase Order")
    public ResponseEntity<ApiResponse> createInvoice(@Valid @RequestBody InvoiceCreateDTO createDTO) {
        InvoiceResponseDTO invoice = invoiceService.createInvoice(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Invoice submitted successfully", invoice));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_MANAGER')")
    @Operation(summary = "Update Invoice Status", description = "Approve, reject, or void an invoice")
    public ResponseEntity<ApiResponse> updateInvoiceStatus(@PathVariable Long id, @RequestParam InvoiceStatus status) {
        InvoiceResponseDTO invoice = invoiceService.updateInvoiceStatus(id, status);
        return ResponseEntity.ok(ApiResponse.ok("Invoice status updated to " + status, invoice));
    }
}
