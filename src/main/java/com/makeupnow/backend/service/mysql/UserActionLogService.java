package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.dto.useractionlog.UserActionLogResponseDTO;
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

    // 🔹 Méthode de mapping UserActionLog → DTO
    public UserActionLogResponseDTO toResponseDTO(UserActionLog log) {
        String username = (log.getUser() != null)
                ? log.getUser().getFirstname() + " " + log.getUser().getLastname()
                : "Anonyme";

        return UserActionLogResponseDTO.builder()
                .id(log.getId())
                .user(username)
                .action(log.getAction())
                .description(log.getDescription())
                .timestamp(log.getTimestamp())
                .anonymized(log.isAnonymized())
                .build();
    }

    // 🔹 Création d’un log d’action
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

    // 🔹 Anonymisation des logs d’un utilisateur
    @PreAuthorize("hasRole('ADMIN')")
    public void anonymizeUserLogs(Long userId) {
        List<UserActionLog> logs = userActionLogRepository.findByUserId(userId);
        for (UserActionLog log : logs) {
            log.setAnonymized(true);
            log.setUser(null);
        }
        userActionLogRepository.saveAll(logs);
    }

    // 🔹 Récupérer tous les logs d’un utilisateur
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserActionLog> getUserActionLogsByUserId(Long userId) {
        return userActionLogRepository.findByUserId(userId);
    }

    // 🔹 Récupérer tous les logs anonymisés
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserActionLog> getAnonymizedUserActionLogs() {
        return userActionLogRepository.findByAnonymizedTrue();
    }

    // 🔹 Récupérer les logs anonymisés d’un utilisateur donné
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserActionLog> getUserAnonymizedLogsByUserId(Long userId) {
        return userActionLogRepository.findByUserIdAndAnonymizedTrue(userId);
    }
}
