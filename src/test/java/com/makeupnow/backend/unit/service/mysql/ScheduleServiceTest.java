package com.makeupnow.backend.unit.service.mysql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.makeupnow.backend.dto.schedule.ScheduleCreateDTO;
import com.makeupnow.backend.dto.schedule.ScheduleUpdateDTO;
import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.model.mysql.Schedule;
import com.makeupnow.backend.repository.mysql.ProviderRepository;
import com.makeupnow.backend.repository.mysql.ScheduleRepository;
import com.makeupnow.backend.service.mysql.ScheduleService;
import com.makeupnow.backend.security.SecurityUtils;
import com.makeupnow.backend.exception.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.mockito.MockedStatic;  // <--- important pour mock statique

import java.time.LocalDateTime;
import java.util.Optional;

public class ScheduleServiceTest {

    @InjectMocks
    private ScheduleService scheduleService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ProviderRepository providerRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createSchedule_shouldCreate_whenProviderIdMatchesCurrentUser() {
        Long currentUserId = 42L;
        ScheduleCreateDTO dto = new ScheduleCreateDTO();
        dto.setProviderId(currentUserId);
        dto.setStartTime(LocalDateTime.of(2025, 6, 5, 9, 0));
        dto.setEndTime(LocalDateTime.of(2025, 6, 5, 12, 0));

        Provider provider = new Provider();
        provider.setId(currentUserId);

        when(providerRepository.findById(currentUserId)).thenReturn(Optional.of(provider));
        when(scheduleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

            var result = scheduleService.createSchedule(dto);

            assertNotNull(result);
            assertEquals(dto.getStartTime(), result.getStartTime());
            assertEquals(dto.getEndTime(), result.getEndTime());
            assertEquals(currentUserId, result.getProviderId());

            verify(scheduleRepository).save(any(Schedule.class));
        }
    }

    @Test
    void createSchedule_shouldThrowSecurityException_whenProviderIdDiffers() {
        Long currentUserId = 42L;
        Long wrongProviderId = 99L;
        ScheduleCreateDTO dto = new ScheduleCreateDTO();
        dto.setProviderId(wrongProviderId);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

            SecurityException ex = assertThrows(SecurityException.class, () -> scheduleService.createSchedule(dto));
            assertEquals("Accès interdit : vous ne pouvez créer un créneau que pour votre propre compte.", ex.getMessage());
        }
    }

    @Test
    void createSchedule_shouldThrowResourceNotFoundException_whenProviderNotFound() {
        Long currentUserId = 42L;
        ScheduleCreateDTO dto = new ScheduleCreateDTO();
        dto.setProviderId(currentUserId);

        when(providerRepository.findById(currentUserId)).thenReturn(Optional.empty());

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> scheduleService.createSchedule(dto));
            assertEquals("Prestataire non trouvé.", ex.getMessage());
        }
    }

    @Test
    void updateSchedule_shouldUpdate_whenAuthorized() {
        Long currentUserId = 42L;
        Long scheduleId = 100L;

        ScheduleUpdateDTO dto = new ScheduleUpdateDTO();
        dto.setStartTime(LocalDateTime.of(2025, 6, 6, 14, 0));
        dto.setEndTime(LocalDateTime.of(2025, 6, 6, 16, 0));

        Provider provider = new Provider();
        provider.setId(currentUserId);

        Schedule schedule = Schedule.builder()
                .id(scheduleId)
                .provider(provider)
                .startTime(LocalDateTime.of(2025, 6, 6, 9, 0))
                .endTime(LocalDateTime.of(2025, 6, 6, 12, 0))
                .build();

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));
        when(scheduleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

            var updated = scheduleService.updateSchedule(scheduleId, dto);

            assertEquals(dto.getStartTime(), updated.getStartTime());
            assertEquals(dto.getEndTime(), updated.getEndTime());
            verify(scheduleRepository).save(schedule);
        }
    }

    @Test
    void updateSchedule_shouldThrowSecurityException_whenNotOwner() {
        Long currentUserId = 42L;
        Long scheduleId = 100L;

        ScheduleUpdateDTO dto = new ScheduleUpdateDTO();
        dto.setStartTime(LocalDateTime.now());
        dto.setEndTime(LocalDateTime.now().plusHours(3));

        Provider otherProvider = new Provider();
        otherProvider.setId(99L); // Différent de currentUserId

        Schedule schedule = Schedule.builder()
                .id(scheduleId)
                .provider(otherProvider)
                .build();

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

            SecurityException ex = assertThrows(SecurityException.class, () -> {
                scheduleService.updateSchedule(scheduleId, dto);
            });

            assertEquals("Accès interdit : vous ne pouvez modifier que vos propres créneaux.", ex.getMessage());
        }
    }

    @Test
    void updateSchedule_shouldThrowNotFound_whenScheduleMissing() {
        Long scheduleId = 100L;

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(42L);

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> scheduleService.updateSchedule(scheduleId, new ScheduleUpdateDTO()));
            assertEquals("Créneau introuvable.", ex.getMessage());
        }
    }

    @Test
    void deleteSchedule_shouldDelete_whenAuthorized() {
        Long currentUserId = 42L;
        Long scheduleId = 100L;

        Provider provider = new Provider();
        provider.setId(currentUserId);

        Schedule schedule = Schedule.builder()
                .id(scheduleId)
                .provider(provider)
                .build();

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

            boolean result = scheduleService.deleteSchedule(scheduleId);

            assertTrue(result);
            verify(scheduleRepository).delete(schedule);
        }
    }

    @Test
    void deleteSchedule_shouldThrowSecurityException_whenNotOwner() {
        Long currentUserId = 42L;
        Long scheduleId = 100L;

        Provider otherProvider = new Provider();
        otherProvider.setId(99L);

        Schedule schedule = Schedule.builder()
                .id(scheduleId)
                .provider(otherProvider)
                .build();

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

            SecurityException ex = assertThrows(SecurityException.class, () -> {
                scheduleService.deleteSchedule(scheduleId);
            });

            assertEquals("Accès interdit : vous ne pouvez supprimer que vos propres créneaux.", ex.getMessage());
        }
    }

    @Test
    void deleteSchedule_shouldThrowNotFound_whenScheduleMissing() {
        Long scheduleId = 100L;

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(42L);

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> scheduleService.deleteSchedule(scheduleId));
            assertEquals("Créneau introuvable.", ex.getMessage());
        }
    }
}
