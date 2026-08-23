package com.smartprocure.service;

import com.smartprocure.domain.entity.Payment;
import com.smartprocure.domain.enums.InvoiceStatus;
import com.smartprocure.dto.request.InvoiceCreateDTO;
import com.smartprocure.dto.response.InvoiceResponseDTO;

import java.util.List;

public interface InvoiceService {
    InvoiceResponseDTO createInvoice(InvoiceCreateDTO createDTO);
    InvoiceResponseDTO updateInvoiceStatus(Long id, InvoiceStatus status);
    List<InvoiceResponseDTO> getAllInvoices();
    List<Payment> getAllPayments();
    InvoiceResponseDTO getInvoiceById(Long id);
}
