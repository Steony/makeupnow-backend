package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.dto.schedule.ScheduleCreateDTO;
import com.makeupnow.backend.dto.schedule.ScheduleUpdateDTO;
import com.makeupnow.backend.dto.schedule.ScheduleResponseDTO;
import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.model.mysql.Schedule;
import com.makeupnow.backend.repository.mysql.ProviderRepository;
import com.makeupnow.backend.repository.mysql.ScheduleRepository;
import com.makeupnow.backend.security.SecurityUtils;

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
        Long currentUserId = SecurityUtils.getCurrentUserId();

        if (!dto.getProviderId().equals(currentUserId)) {
            throw new SecurityException("Accès interdit : vous ne pouvez créer un créneau que pour votre propre compte.");
        }

        Provider provider = providerRepository.findById(dto.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Prestataire non trouvé."));

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
        Long currentUserId = SecurityUtils.getCurrentUserId();

        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau introuvable."));

        if (!schedule.getProvider().getId().equals(currentUserId)) {
            throw new SecurityException("Accès interdit : vous ne pouvez modifier que vos propres créneaux.");
        }

        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());

        return mapToDTO(scheduleRepository.save(schedule));
    }

    @PreAuthorize("hasRole('PROVIDER')")
    @Transactional
    public boolean deleteSchedule(Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau introuvable."));

        if (!schedule.getProvider().getId().equals(currentUserId)) {
            throw new SecurityException("Accès interdit : vous ne pouvez supprimer que vos propres créneaux.");
        }

        scheduleRepository.delete(schedule);
        return true;
    }

    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    public List<ScheduleResponseDTO> getSchedulesByProvider(Long providerId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        if (SecurityUtils.isCurrentUserProvider() && !providerId.equals(currentUserId)) {
            throw new SecurityException("Accès interdit : un prestataire ne peut voir que ses propres créneaux.");
        }

        return scheduleRepository.findByProviderId(providerId)
                .stream().map(this::mapToDTO).toList();
    }

    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    public List<ScheduleResponseDTO> getAvailableSchedules(Long serviceId, String date) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        if (SecurityUtils.isCurrentUserProvider()) {
            boolean ownsService = providerRepository.findById(currentUserId)
                    .orElseThrow(() -> new RuntimeException("Prestataire non trouvé."))
                    .getServices()
                    .stream()
                    .anyMatch(service -> service.getId().equals(serviceId));

            if (!ownsService) {
                throw new SecurityException("Accès interdit à un service qui ne vous appartient pas.");
            }
        }

        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        return scheduleRepository.findByProvider_Services_IdAndStartTimeBetween(serviceId, startOfDay, endOfDay)
                .stream().map(this::mapToDTO).toList();
    }

    private ScheduleResponseDTO mapToDTO(Schedule schedule) {
        ScheduleResponseDTO dto = new ScheduleResponseDTO();
        dto.setId(schedule.getId());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setProviderId(schedule.getProvider().getId());
        return dto;
    }
}
