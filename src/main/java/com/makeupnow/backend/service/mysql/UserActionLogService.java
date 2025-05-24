package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.model.mysql.User;
import com.makeupnow.backend.model.mysql.UserActionLog;
import com.makeupnow.backend.repository.mysql.UserActionLogRepository;
import com.makeupnow.backend.repository.mysql.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserActionLogService {

    @Autowired
    private UserActionLogRepository userActionLogRepository;

    @Autowired
    private UserRepository userRepository;

    // Création d’un log d’action
    @PreAuthorize("hasRole('ADMIN')")
    public void logActionByUserId(Long userId, String action, String description) {
        User user = userRepository.findById(userId).orElse(null);

        UserActionLog log = UserActionLog.builder()
            .user(user)
            .action(action)
            .description(description)
            .timestamp(LocalDateTime.now())
            .anonymized(false)
            .build();

        userActionLogRepository.save(log);
    }

    // Anonymiser les logs d’un utilisateur
    @PreAuthorize("hasRole('ADMIN')")
    public void anonymizeUserLogs(Long userId) {
        List<UserActionLog> logs = userActionLogRepository.findByUserId(userId);
        for (UserActionLog log : logs) {
            log.setAnonymized(true);
            log.setUser(null);
        }
        userActionLogRepository.saveAll(logs);
    }

    // Méthode pour récupérer les logs d'un utilisateur
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserActionLog> getUserActionLogsByUserId(Long userId) {
        return userActionLogRepository.findByUserId(userId);
    }

    // Méthode pour récupérer les logs anonymisés
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserActionLog> getAnonymizedUserActionLogs() {
        return userActionLogRepository.findByAnonymizedTrue();
    }

    // Méthode pour récupérer les logs anonymisés d'un utilisateur donné
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserActionLog> getUserAnonymizedLogsByUserId(Long userId) {
        return userActionLogRepository.findByUserIdAndAnonymizedTrue(userId);
    }
}
