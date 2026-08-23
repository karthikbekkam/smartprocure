package com.smartprocure.service;

import com.smartprocure.dto.request.PaymentCreateDTO;
import com.smartprocure.dto.response.PaymentResponseDTO;

import java.util.List;

public interface PaymentService {
    PaymentResponseDTO processPayment(PaymentCreateDTO paymentCreateDTO, String username);
    List<PaymentResponseDTO> getAllPayments();
    PaymentResponseDTO getPaymentById(Long id);
}
