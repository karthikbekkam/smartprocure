package com.smartprocure.invoice;

import com.smartprocure.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<InvoiceDto>>> getAllInvoices(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok("Invoices fetched successfully", invoiceService.getAllInvoices(pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceDto>> createInvoice(@Valid @RequestBody InvoiceDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Invoice created", invoiceService.createInvoice(dto)));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<InvoiceDto>> approveInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Invoice approved", invoiceService.approveInvoice(id)));
    }
}
