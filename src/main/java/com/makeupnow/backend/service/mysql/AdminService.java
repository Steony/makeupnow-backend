package com.makeupnow.backend.service.mysql;

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
        return userRepository.findById(userId).orElse(null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public boolean deactivateUser(Long adminId, Long userId) {
        return userRepository.findById(userId).map(user -> {
            user.setActive(false);
            userRepository.save(user);

            userActionLogService.logActionByUserId(adminId, "Désactivation Utilisateur",
                    "L'utilisateur avec ID " + userId + " a été désactivé.");
            return true;
        }).orElse(false);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public boolean reactivateUser(Long adminId, Long userId) {
        return userRepository.findById(userId).map(user -> {
            user.setActive(true);
            userRepository.save(user);

            userActionLogService.logActionByUserId(adminId, "Réactivation Utilisateur",
                    "L'utilisateur avec ID " + userId + " a été réactivé.");
            return true;
        }).orElse(false);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public boolean deleteUser(Long adminId, Long userId) {
        if (userRepository.existsById(userId)) {
            // Anonymiser les logs d'action avant suppression
            userActionLogService.anonymizeUserLogs(userId);

            // Suppression de l'utilisateur
            userRepository.deleteById(userId);

            // Log de suppression par admin
            userActionLogService.logActionByUserId(adminId, "Suppression Utilisateur",
                    "L'utilisateur avec ID " + userId + " a été supprimé.");

            return true;
        }
        return false;
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
