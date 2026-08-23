package com.smartprocure.modules.finance.service;

import com.smartprocure.core.payload.PagedResponse;
import com.smartprocure.modules.finance.dto.InvoiceDTO;

import java.time.LocalDate;

/**
 * Enterprise Service Contract for Accounts Payable Invoice Generation & Management.
 *
 * @author Principal Java Architect
 */
public interface InvoiceService {

    InvoiceDTO generateInvoiceFromPurchaseOrder(Long purchaseOrderId, LocalDate dueDate);

    InvoiceDTO getInvoiceById(Long id);

    InvoiceDTO getInvoiceByNumber(String invoiceNumber);

    PagedResponse<InvoiceDTO> getAllInvoices(int page, int size, String sortBy, String sortDir, String status);

    void cancelInvoice(Long id);
}
