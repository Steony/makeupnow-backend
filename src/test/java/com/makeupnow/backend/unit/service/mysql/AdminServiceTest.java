package com.makeupnow.backend.unit.service.mysql;

import com.makeupnow.backend.exception.ResourceNotFoundException;
import com.makeupnow.backend.model.mysql.Customer;
import com.makeupnow.backend.model.mysql.User;
import com.makeupnow.backend.repository.mysql.CustomerRepository;
import com.makeupnow.backend.repository.mysql.ProviderRepository;
import com.makeupnow.backend.repository.mysql.UserRepository;
import com.makeupnow.backend.service.mysql.AdminService;
import com.makeupnow.backend.service.mysql.UserActionLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.*;

class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private UserActionLogService userActionLogService;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeactivateUser_Success() {
        Long adminId = 1L;
        Long userId = 2L;
        Customer user = new Customer();
        user.setId(userId);
        user.setActive(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        boolean result = adminService.deactivateUser(adminId, userId);

        assertFalse(user.isActive());
        verify(userRepository).save(user);
        verify(userActionLogService).logActionByUserId(eq(adminId), eq("Désactivation Utilisateur"),
                contains("a été désactivé"));
        assertTrue(result);
    }

    @Test
    void testReactivateUser_Success() {
        Long adminId = 1L;
        Long userId = 2L;
        Customer user = new Customer();
        user.setId(userId);
        user.setActive(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        boolean result = adminService.reactivateUser(adminId, userId);

        assertTrue(user.isActive());
        verify(userRepository).save(user);
        verify(userActionLogService).logActionByUserId(eq(adminId), eq("Réactivation Utilisateur"),
                contains("a été réactivé"));
        assertTrue(result);
    }

    @Test
    void testDeleteUser_Success() {
        Long adminId = 1L;
        Long userId = 2L;

        when(userRepository.existsById(userId)).thenReturn(true);

        boolean result = adminService.deleteUser(adminId, userId);

        verify(userActionLogService).anonymizeUserLogs(userId);
        verify(userRepository).deleteById(userId);
        verify(userActionLogService).logActionByUserId(eq(adminId), eq("Suppression Utilisateur"),
                contains("a été supprimé"));
        assertTrue(result);
    }

    @Test
    void testGetUserById_Success() {
        Long userId = 2L;
        User user = new Customer();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = adminService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository).findById(userId);
    }

    @Test
    void testGetAllUsers_Success() {
        adminService.getAllUsers();
        verify(userRepository).findAll();
    }

    @Test
    void testGetCustomersByStatus_Success() {
        adminService.getCustomersByStatus(true);
        verify(customerRepository).findByIsActive(true);
    }

    @Test
    void testGetProvidersByStatus_Success() {
        adminService.getProvidersByStatus(false);
        verify(providerRepository).findByIsActive(false);
    }

    // 👉 Tests d’exception pour la priorité 4 :

    @Test
    void testGetUserById_UserNotFound() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.getUserById(userId));
    }

    @Test
    void testDeactivateUser_UserNotFound() {
        Long adminId = 1L;
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.deactivateUser(adminId, userId));
    }

    @Test
    void testReactivateUser_UserNotFound() {
        Long adminId = 1L;
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.reactivateUser(adminId, userId));
    }

    @Test
    void testDeleteUser_UserNotFound() {
        Long adminId = 1L;
        Long userId = 99L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> adminService.deleteUser(adminId, userId));
    }
}
