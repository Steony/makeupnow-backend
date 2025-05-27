package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.dto.schedule.ScheduleCreateDTO;
import com.makeupnow.backend.dto.schedule.ScheduleUpdateDTO;
import com.makeupnow.backend.dto.schedule.ScheduleResponseDTO;
import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.model.mysql.Schedule;
import com.makeupnow.backend.repository.mysql.ProviderRepository;
import com.makeupnow.backend.repository.mysql.ScheduleRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @PreAuthorize("hasRole('PROVIDER')")
    @Transactional
    public ScheduleResponseDTO createSchedule(ScheduleCreateDTO dto) {
        Provider provider = providerRepository.findById(dto.getProviderId())
                .orElseThrow(() -> new RuntimeException("Prestataire non trouvé."));

        Schedule schedule = Schedule.builder()
                .provider(provider)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .build();

        return mapToDTO(scheduleRepository.save(schedule));
    }

    @PreAuthorize("hasRole('PROVIDER')")
    @Transactional
    public ScheduleResponseDTO updateSchedule(Long id, ScheduleUpdateDTO dto) {
        return scheduleRepository.findById(id).map(schedule -> {
            schedule.setStartTime(dto.getStartTime());
            schedule.setEndTime(dto.getEndTime());
            return mapToDTO(scheduleRepository.save(schedule));
        }).orElseThrow(() -> new RuntimeException("Créneau introuvable."));
    }

    @PreAuthorize("hasRole('PROVIDER')")
    @Transactional
    public boolean deleteSchedule(Long id) {
        if (scheduleRepository.existsById(id)) {
            scheduleRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @PreAuthorize("hasRole('PROVIDER')")
    public List<ScheduleResponseDTO> getSchedulesByProvider(Long providerId) {
        return scheduleRepository.findByProviderId(providerId)
                .stream().map(this::mapToDTO).toList();
    }

    @PreAuthorize("isAuthenticated()")
    public List<ScheduleResponseDTO> getAvailableSchedules(Long serviceId, String date) {
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        return scheduleRepository.findByProvider_Services_IdAndStartTimeBetween(serviceId, startOfDay, endOfDay)
                .stream().map(this::mapToDTO).toList();
    }

    // 🔁 Méthode de mapping centralisée
    private ScheduleResponseDTO mapToDTO(Schedule schedule) {
        ScheduleResponseDTO dto = new ScheduleResponseDTO();
        dto.setId(schedule.getId());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setProviderId(schedule.getProvider().getId());
        return dto;
    }
}
