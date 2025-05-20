package com.makeupnow.backend.repository.mysql;

import com.makeupnow.backend.model.mysql.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBookingCustomerId(Long customerId);
    List<Payment> findByBookingProviderId(Long providerId);
}
