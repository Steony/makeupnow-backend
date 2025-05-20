package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.model.mysql.Payment;
import com.makeupnow.backend.model.mysql.enums.PaymentStatus;
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
    public ResponseEntity<Payment> createPayment(@RequestParam Long bookingId,
                                                 @RequestParam double amount,
                                                 @RequestParam PaymentStatus status) {
        Payment payment = paymentService.createPayment(bookingId, amount, status);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/confirm/customer")
    public ResponseEntity<Boolean> confirmPaymentByCustomer(@RequestParam Long paymentId,
                                                            @RequestParam Long customerId) {
        boolean success = paymentService.confirmPaymentByCustomer(paymentId, customerId);
        return ResponseEntity.ok(success);
    }

    @PostMapping("/confirm/provider")
    public ResponseEntity<Boolean> confirmPaymentByProvider(@RequestParam Long paymentId,
                                                            @RequestParam Long providerId) {
        boolean success = paymentService.confirmPaymentByProvider(paymentId, providerId);
        return ResponseEntity.ok(success);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Payment>> getPaymentsByCustomer(@PathVariable Long customerId) {
        List<Payment> payments = paymentService.getPaymentsByCustomer(customerId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<Payment>> getPaymentsByProvider(@PathVariable Long providerId) {
        List<Payment> payments = paymentService.getPaymentsByProvider(providerId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/update-status")
    public ResponseEntity<Boolean> updatePaymentStatus(@RequestParam Long paymentId,
                                                       @RequestParam PaymentStatus status,
                                                       @RequestParam Long adminId) {
        boolean success = paymentService.updatePaymentStatus(paymentId, status, adminId);
        return ResponseEntity.ok(success);
    }
}
