package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.model.mysql.Customer;
import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.model.mysql.User;
import com.makeupnow.backend.model.mysql.UserActionLog;
import com.makeupnow.backend.repository.mysql.CustomerRepository;
import com.makeupnow.backend.repository.mysql.ProviderRepository;
import com.makeupnow.backend.repository.mysql.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private UserActionLogService userActionLogService;

    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public boolean deactivateUser(Long adminId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + userId));

        user.setActive(false);
        userRepository.save(user);

        userActionLogService.logActionByUserId(adminId, "Désactivation Utilisateur",
                "L'utilisateur avec ID " + userId + " a été désactivé.");
        return true;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public boolean reactivateUser(Long adminId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + userId));

        user.setActive(true);
        userRepository.save(user);

        userActionLogService.logActionByUserId(adminId, "Réactivation Utilisateur",
                "L'utilisateur avec ID " + userId + " a été réactivé.");
        return true;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public boolean deleteUser(Long adminId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + userId);
        }

        // Anonymiser les logs avant suppression
        userActionLogService.anonymizeUserLogs(userId);

        // Supprimer l'utilisateur
        userRepository.deleteById(userId);

        userActionLogService.logActionByUserId(adminId, "Suppression Utilisateur",
                "L'utilisateur avec ID " + userId + " a été supprimé.");

        return true;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Customer> getCustomersByStatus(boolean status) {
        return customerRepository.findByIsActive(status);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Provider> getProvidersByStatus(boolean status) {
        return providerRepository.findByIsActive(status);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserActionLog> getUserActionLogsByUserId(Long userId) {
        return userActionLogService.getUserActionLogsByUserId(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserActionLog> getAnonymizedUserActionLogs() {
        return userActionLogService.getAnonymizedUserActionLogs();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserActionLog> getUserAnonymizedLogsByUserId(Long userId) {
        return userActionLogService.getUserAnonymizedLogsByUserId(userId);
    }
}
