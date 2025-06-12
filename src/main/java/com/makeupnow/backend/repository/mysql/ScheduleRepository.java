package com.makeupnow.backend.repository.mysql;

import com.makeupnow.backend.model.mysql.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByProviderId(Long providerId);

    List<Schedule> findByStartTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Recherche des créneaux associés à un service et dans une période donnée
    List<Schedule> findByProvider_Services_IdAndStartTimeBetween(Long serviceId, LocalDateTime startDate, LocalDateTime endDate);
List<Schedule> findByProviderIdAndStartTimeBetween(Long providerId, LocalDateTime start, LocalDateTime end);



// Récupère tous les créneaux d’un provider qui n'ont pas encore de réservation (bookings vide)

@Query("""
    SELECT s FROM Schedule s
    WHERE s.provider.id = :providerId
      AND s.startTime > CURRENT_TIMESTAMP
      AND (
            NOT EXISTS (
                SELECT 1 FROM Booking b
                WHERE b.schedule = s
                  AND b.status IN ('CONFIRMED', 'COMPLETED')
            )
          )
""")
List<Schedule> findAvailableSchedulesByProviderId(@Param("providerId") Long providerId);




}
