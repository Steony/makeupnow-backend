package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.dto.useractionlog.UserActionLogResponseDTO;
import com.makeupnow.backend.service.mysql.UserActionLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-action-logs")
public class UserActionLogController {

    @Autowired
    private UserActionLogService userActionLogService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/anonymized")
    public ResponseEntity<List<UserActionLogResponseDTO>> getAnonymizedLogs() {
        List<UserActionLogResponseDTO> dtos = userActionLogService.getAnonymizedUserActionLogs()
            .stream()
            .map(userActionLogService::toResponseDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserActionLogResponseDTO>> getLogsByUser(@PathVariable Long userId) {
        List<UserActionLogResponseDTO> dtos = userActionLogService.getUserActionLogsByUserId(userId)
            .stream()
            .map(userActionLogService::toResponseDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}/anonymized")
    public ResponseEntity<List<UserActionLogResponseDTO>> getAnonymizedLogsByUser(@PathVariable Long userId) {
        List<UserActionLogResponseDTO> dtos = userActionLogService.getUserAnonymizedLogsByUserId(userId)
            .stream()
            .map(userActionLogService::toResponseDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
