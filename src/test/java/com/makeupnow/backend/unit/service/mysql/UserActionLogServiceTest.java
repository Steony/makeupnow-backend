package com.makeupnow.backend.unit.service.mysql;

import com.makeupnow.backend.model.mysql.Customer;
import com.makeupnow.backend.model.mysql.UserActionLog;
import com.makeupnow.backend.repository.mysql.UserActionLogRepository;
import com.makeupnow.backend.repository.mysql.UserRepository;
import com.makeupnow.backend.service.mysql.UserActionLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserActionLogServiceTest {

    @InjectMocks
    private UserActionLogService userActionLogService;

    @Mock
    private UserActionLogRepository userActionLogRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
void anonymizeUserLogs_shouldAnonymizeLogs() {
    // Arrange
    Long userId = 100L;

    // Comme User est abstrait, on instancie un Customer (hérite de User)
    Customer customer = new Customer();
    customer.setId(userId);
    customer.setFirstname("John");
    customer.setLastname("Doe");

    UserActionLog log1 = UserActionLog.builder()
            .id(1L)
            .user(customer)
            .anonymized(false)
            .build();

    UserActionLog log2 = UserActionLog.builder()
            .id(2L)
            .user(customer)
            .anonymized(false)
            .build();

    List<UserActionLog> logs = List.of(log1, log2);

    when(userActionLogRepository.findByUserId(userId)).thenReturn(logs);

    // Act
    userActionLogService.anonymizeUserLogs(userId);

    // Assert
    assertTrue(log1.isAnonymized());
    assertNull(log1.getUser());
    assertTrue(log2.isAnonymized());
    assertNull(log2.getUser());

    verify(userActionLogRepository).saveAll(logs);
}

@Test
void anonymizeUserLogs_shouldHandleNoLogs() {
    Long userId = 101L;
    when(userActionLogRepository.findByUserId(userId)).thenReturn(List.of());

    // Act
    userActionLogService.anonymizeUserLogs(userId);

    // Assert
    verify(userActionLogRepository).findByUserId(userId);
    verify(userActionLogRepository).saveAll(eq(List.of()));

}


}