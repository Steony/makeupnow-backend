package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.dto.schedule.ScheduleCreateDTO;
import com.makeupnow.backend.dto.schedule.ScheduleUpdateDTO;
import com.makeupnow.backend.dto.schedule.ScheduleResponseDTO;
import com.makeupnow.backend.service.mysql.ScheduleService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;
@PreAuthorize("hasRole('PROVIDER')")
    @PostMapping
    public ResponseEntity<ScheduleResponseDTO> createSchedule(@RequestBody @Valid ScheduleCreateDTO dto) {
        ScheduleResponseDTO response = scheduleService.createSchedule(dto);
        return ResponseEntity.ok(response);
    }
@PreAuthorize("hasRole('PROVIDER')")
    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponseDTO> updateSchedule(@PathVariable Long id, @RequestBody @Valid ScheduleUpdateDTO dto) {
        ScheduleResponseDTO updated = scheduleService.updateSchedule(id, dto);
        return ResponseEntity.ok(updated);
    }
@PreAuthorize("hasRole('PROVIDER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSchedule(@PathVariable Long id) {
        boolean deleted = scheduleService.deleteSchedule(id);
        if (deleted) {
            return ResponseEntity.ok("Créneau supprimé avec succès.");
        } else {
            return ResponseEntity.status(404).body("Créneau introuvable.");
        }
    }
@PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<ScheduleResponseDTO>> getSchedulesByProvider(@PathVariable Long providerId) {
        List<ScheduleResponseDTO> list = scheduleService.getSchedulesByProvider(providerId);
        return ResponseEntity.ok(list);
    }
@PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
    @GetMapping("/available")
    public ResponseEntity<List<ScheduleResponseDTO>> getAvailableSchedules(
            @RequestParam Long serviceId,
            @RequestParam String date) {
        List<ScheduleResponseDTO> list = scheduleService.getAvailableSchedules(serviceId, date);
        return ResponseEntity.ok(list);
    }

    @PreAuthorize("hasAnyRole('CLIENT','PROVIDER','ADMIN')")
@GetMapping("/provider/{providerId}/available")
public ResponseEntity<List<ScheduleResponseDTO>> getAvailableSchedulesByProvider(@PathVariable Long providerId) {
    List<ScheduleResponseDTO> list = scheduleService.getAvailableSchedulesByProvider(providerId);
    return ResponseEntity.ok(list);
}

  
}
