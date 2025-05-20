package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.model.mysql.Schedule;
import com.makeupnow.backend.service.mysql.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@RequestParam Long providerId,
                                                   @RequestParam LocalDateTime startTime,
                                                   @RequestParam LocalDateTime endTime) {
        Schedule schedule = scheduleService.createSchedule(providerId, startTime, endTime);
        return ResponseEntity.ok(schedule);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateSchedule(@PathVariable Long id,
                                                  @RequestParam LocalDateTime startTime,
                                                  @RequestParam LocalDateTime endTime) {
        boolean updated = scheduleService.updateSchedule(id, startTime, endTime);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteSchedule(@PathVariable Long id) {
        boolean deleted = scheduleService.deleteSchedule(id);
        return ResponseEntity.ok(deleted);
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<Schedule>> getSchedulesByProvider(@PathVariable Long providerId) {
        List<Schedule> schedules = scheduleService.getSchedulesByProvider(providerId);
        return ResponseEntity.ok(schedules);
    }

    // Nouveau endpoint pour les créneaux disponibles par service et date
    @GetMapping("/available")
    public ResponseEntity<List<Schedule>> getAvailableSchedules(
            @RequestParam Long serviceId,
            @RequestParam String date) {  // en string pour recevoir un format ISO "2025-05-20"
        LocalDate localDate = LocalDate.parse(date);
        List<Schedule> schedules = scheduleService.getAvailableSchedules(serviceId, localDate);
        return ResponseEntity.ok(schedules);
    }
}
