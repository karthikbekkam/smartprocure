package com.smartprocure.invoice;

import com.smartprocure.common.exception.ResourceNotFoundException;
import com.smartprocure.domain.Invoice;
import com.smartprocure.domain.PurchaseOrder;
import com.smartprocure.domain.Vendor;
import com.smartprocure.domain.repository.InvoiceRepository;
import com.smartprocure.domain.repository.PurchaseOrderRepository;
import com.smartprocure.domain.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final VendorRepository vendorRepository;

    @Transactional(readOnly = true)
    public Page<InvoiceDto> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional
    public InvoiceDto createInvoice(InvoiceDto dto) {
        PurchaseOrder po = purchaseOrderRepository.findById(dto.getPurchaseOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", dto.getPurchaseOrderId()));

        Vendor vendor = vendorRepository.findById(dto.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", dto.getVendorId()));

        String invoiceNumber = "INV-" + System.currentTimeMillis();

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .purchaseOrder(po)
                .vendor(vendor)
                .invoiceDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(30))
                .totalAmount(dto.getTotalAmount() != null ? dto.getTotalAmount() : po.getTotalAmount())
                .paidAmount(BigDecimal.ZERO)
                .status("SUBMITTED")
                .build();

        return mapToDto(invoiceRepository.save(invoice));
    }

    @Transactional
    public InvoiceDto approveInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
        invoice.setStatus("APPROVED");
        return mapToDto(invoiceRepository.save(invoice));
    }

    private InvoiceDto mapToDto(Invoice inv) {
        return InvoiceDto.builder()
                .id(inv.getId())
                .invoiceNumber(inv.getInvoiceNumber())
                .purchaseOrderId(inv.getPurchaseOrder().getId())
                .poNumber(inv.getPurchaseOrder().getPoNumber())
                .vendorId(inv.getVendor().getId())
                .vendorName(inv.getVendor().getCompanyName())
                .invoiceDate(inv.getInvoiceDate())
                .dueDate(inv.getDueDate())
                .totalAmount(inv.getTotalAmount())
                .paidAmount(inv.getPaidAmount())
                .status(inv.getStatus())
                .build();
    }
}
