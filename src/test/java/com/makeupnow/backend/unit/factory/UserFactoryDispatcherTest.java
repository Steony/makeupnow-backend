package com.makeupnow.backend.unit.factory;

import com.makeupnow.backend.factory.*;
import com.makeupnow.backend.model.mysql.Customer;
import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.model.mysql.User;
import com.makeupnow.backend.model.mysql.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserFactoryDispatcherTest {

    private AdminFactory adminFactory;
    private CustomerFactory customerFactory;
    private ProviderFactory providerFactory;
    private UserFactoryDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        adminFactory = mock(AdminFactory.class);
        customerFactory = mock(CustomerFactory.class);
        providerFactory = mock(ProviderFactory.class);

        dispatcher = new UserFactoryDispatcher(adminFactory, customerFactory, providerFactory);
    }

    @Test
    void testCreateUser_Admin() {
        when(adminFactory.createUser("Admin", "Admin", "admin@test.com", "pass"))
                .thenReturn(mock(User.class));

        User user = dispatcher.createUser(Role.ADMIN, "Admin", "Admin", "admin@test.com", "pass");

        assertNotNull(user);
        verify(adminFactory).createUser("Admin", "Admin", "admin@test.com", "pass");
    }

    @Test
    void testCreateUser_Client() {
        when(customerFactory.createUser("Client", "Test", "client@test.com", "pass"))
                .thenReturn(new Customer());

        User user = dispatcher.createUser(Role.CLIENT, "Client", "Test", "client@test.com", "pass");

        assertNotNull(user);
        assertTrue(user instanceof Customer);
        verify(customerFactory).createUser("Client", "Test", "client@test.com", "pass");
    }

    @Test
    void testCreateUser_Provider() {
        when(providerFactory.createUser("Provider", "Test", "provider@test.com", "pass"))
                .thenReturn(new Provider());

        User user = dispatcher.createUser(Role.PROVIDER, "Provider", "Test", "provider@test.com", "pass");

        assertNotNull(user);
        assertTrue(user instanceof Provider);
        verify(providerFactory).createUser("Provider", "Test", "provider@test.com", "pass");
    }

    @Test
    void testCreateUser_RoleNonDefini() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            dispatcher.createUser(null, "A", "B", "x@y.com", "pass");
        });

        assertTrue(exception.getMessage().contains("Factory non définie"));
    }
}
