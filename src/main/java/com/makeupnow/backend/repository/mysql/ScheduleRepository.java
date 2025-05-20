package com.makeupnow.backend.repository.mysql;

import com.makeupnow.backend.model.mysql.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByProviderId(Long providerId);

    List<Schedule> findByStartTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Recherche des créneaux associés à un service et dans une période donnée
    List<Schedule> findByProvider_Services_IdAndStartTimeBetween(Long serviceId, LocalDateTime startDate, LocalDateTime endDate);

}
