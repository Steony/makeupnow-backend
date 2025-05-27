package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.dto.payment.PaymentCreateDTO;
import com.makeupnow.backend.dto.payment.PaymentResponseDTO;
import com.makeupnow.backend.dto.payment.PaymentStatusUpdateDTO;
import com.makeupnow.backend.service.mysql.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<PaymentResponseDTO> createPayment(@RequestBody PaymentCreateDTO dto) {
        PaymentResponseDTO payment = paymentService.createPayment(dto);
        return ResponseEntity.status(201).body(payment);
    }

    @PostMapping("/confirm/customer")
    public ResponseEntity<Boolean> confirmPaymentByCustomer(
            @RequestParam Long paymentId,
            @RequestParam Long customerId) {
        boolean success = paymentService.confirmPaymentByCustomer(paymentId, customerId);
        return ResponseEntity.ok(success);
    }

    @PostMapping("/confirm/provider")
    public ResponseEntity<Boolean> confirmPaymentByProvider(
            @RequestParam Long paymentId,
            @RequestParam Long providerId) {
        boolean success = paymentService.confirmPaymentByProvider(paymentId, providerId);
        return ResponseEntity.ok(success);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByCustomer(@PathVariable Long customerId) {
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByCustomer(customerId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByProvider(@PathVariable Long providerId) {
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByProvider(providerId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments() {
        List<PaymentResponseDTO> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/update-status")
    public ResponseEntity<Boolean> updatePaymentStatus(@RequestBody PaymentStatusUpdateDTO dto) {
        boolean success = paymentService.updatePaymentStatus(dto);

        return ResponseEntity.ok(success);
    }
}
