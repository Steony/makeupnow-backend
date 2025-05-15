package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.model.mysql.Customer;
import com.makeupnow.backend.model.mysql.enums.Role;
import com.makeupnow.backend.repository.mysql.CustomerRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock(lenient = true)
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    // ✅ Test 1 : Récupérer tous les clients
    @Test
    void shouldReturnAllCustomers() {
        // Given
        Customer customer1 = new Customer(1L, "John", "Doe", "john.doe@example.com", null, null, null, null, true);
        Customer customer2 = new Customer(2L, "Jane", "Smith", "jane.smith@example.com", null, null, null, null, true);
        when(customerRepository.findAll()).thenReturn(List.of(customer1, customer2));

        // When
        List<Customer> customers = customerService.getAllCustomers();

        // Then
        assertThat(customers).hasSize(2);
        assertThat(customers.get(0).getFirstname()).isEqualTo("John");
        assertThat(customers.get(1).getFirstname()).isEqualTo("Jane");
    }

    // ✅ Test 2 : Récupérer un client par ID
    @Test
    void shouldReturnCustomerById() {
        // Given
        Customer customer = new Customer(1L, "John", "Doe", "john.doe@example.com", null, null, null, null, true);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // When
        Customer result = customerService.getCustomerById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstname()).isEqualTo("John");
        assertThat(result.getLastname()).isEqualTo("Doe");
    }

    // ✅ Test 3 : Enregistrer un nouveau client (save)
    @Test
    void shouldSaveCustomer() {
        // Given
        Customer customer = new Customer(null, "Alice", "Wonder", "alice.wonder@example.com", null, null, null, null, true);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // When
        Customer savedCustomer = customerService.saveCustomer(customer);

        // Then
        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getFirstname()).isEqualTo("Alice");
    }

   // ✅ Test 4 : Mettre à jour un client existant (save)
@Test
void shouldUpdateExistingCustomer() {
    // Given
    Customer existingCustomer = new Customer(1L, "John", "Doe", "john.doe@example.com", "password123", 
                                             "123 Main St", "0123456789", Role.CLIENT, true);
    Customer updatedCustomer = new Customer(1L, "John", "Doe", "john.doe@updated.com", "password123", 
                                            "123 Main St", "0123456789", Role.CLIENT, true);

    when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer)); 
    when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

    // When
    Customer result = customerService.updateCustomer(1L, updatedCustomer);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo("john.doe@updated.com");
}



    // ✅ Test 5 : Désactiver un client (setCustomerActiveStatus)
    @Test
    void shouldDeactivateCustomer() {
        // Given
        Customer customer = new Customer(1L, "John", "Doe", "john.doe@example.com", null, null, null, null, true);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // When
        customerService.setCustomerActiveStatus(1L, false);

        // Then
        verify(customerRepository, times(1)).save(customer);
        assertThat(customer.isIsActive()).isFalse();
    }

    // ✅ Test 6 : Supprimer un client (delete)
    @Test
    void shouldDeleteCustomer() {
        // Given
        Long customerId = 1L;
        doNothing().when(customerRepository).deleteById(customerId);

        // When
        customerService.deleteCustomer(customerId);

        // Then
        verify(customerRepository, times(1)).deleteById(customerId);
    }
}
