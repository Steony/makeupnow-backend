package com.makeupnow.backend.controller.mysql;

import com.makeupnow.backend.model.mysql.UserActionLog;
import com.makeupnow.backend.service.mysql.UserActionLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-action-logs")
public class UserActionLogController {

    @Autowired
    private UserActionLogService userActionLogService;

@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/anonymized")
    public ResponseEntity<List<UserActionLog>> getAnonymizedLogs() {
        List<UserActionLog> logs = userActionLogService.getAnonymizedUserActionLogs();
        return ResponseEntity.ok(logs);
    }

@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserActionLog>> getLogsByUser(@PathVariable Long userId) {
        List<UserActionLog> logs = userActionLogService.getUserActionLogsByUserId(userId);
        return ResponseEntity.ok(logs);
    }

@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}/anonymized")
    public ResponseEntity<List<UserActionLog>> getAnonymizedLogsByUser(@PathVariable Long userId) {
        List<UserActionLog> logs = userActionLogService.getUserAnonymizedLogsByUserId(userId);
        return ResponseEntity.ok(logs);
    }
}
