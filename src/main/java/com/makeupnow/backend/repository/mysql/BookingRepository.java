package com.makeupnow.backend.repository.mysql;

import com.makeupnow.backend.model.mysql.Booking;
import com.makeupnow.backend.model.mysql.enums.BookingStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    List<Booking> findByCustomerId(Long customerId);

    List<Booking> findByProviderId(Long providerId);

    boolean existsByScheduleIdAndStatusNot(Long scheduleId, BookingStatus status);


}
