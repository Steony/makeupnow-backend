package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.model.mysql.Schedule;
import com.makeupnow.backend.repository.mysql.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @PreAuthorize("hasRole('PROVIDER')")
    @Transactional
    public Schedule createSchedule(Long providerId, LocalDateTime startTime, LocalDateTime endTime) {
        Schedule schedule = Schedule.builder()
            .provider(new Provider(providerId)) // Tu peux créer un constructeur simplifié dans Provider avec juste l’ID
            .startTime(startTime)
            .endTime(endTime)
            .build();
        return scheduleRepository.save(schedule);
    }

    @PreAuthorize("hasRole('PROVIDER')")
    @Transactional
    public boolean updateSchedule(Long scheduleId, LocalDateTime startTime, LocalDateTime endTime) {
        return scheduleRepository.findById(scheduleId).map(schedule -> {
            schedule.setStartTime(startTime);
            schedule.setEndTime(endTime);
            scheduleRepository.save(schedule);
            return true;
        }).orElse(false);
    }

    @PreAuthorize("hasRole('PROVIDER')")
    @Transactional
    public boolean deleteSchedule(Long scheduleId) {
        if(scheduleRepository.existsById(scheduleId)) {
            scheduleRepository.deleteById(scheduleId);
            return true;
        }
        return false;
    }

    @PreAuthorize("hasRole('PROVIDER')")
    public List<Schedule> getSchedulesByProvider(Long providerId) {
        return scheduleRepository.findByProviderId(providerId);
    }

    @PreAuthorize("isAuthenticated()")  // tout utilisateur authentifié peut voir les dispos
    public List<Schedule> getAvailableSchedules(Long serviceId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1); // fin de journée

        return scheduleRepository.findByProvider_Services_IdAndStartTimeBetween(serviceId, startOfDay, endOfDay);
    }

}
