package com.makeupnow.backend.model.mysql;

import com.makeupnow.backend.model.mysql.enums.Role;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class CustomerTest {

    @Test
    
    public void testCustomerGettersAndSetters() {
        // ✅ Création d'un Customer avec le constructeur par défaut
        Customer customer = new Customer();
        customer.setFirstname("Jane");
        customer.setLastname("Smith");
        customer.setEmail("jane.smith@example.com");
        customer.setPassword("password123");
        customer.setPhoneNumber("0987654321");
        customer.setIsActive(true);
        customer.setRole(Role.CLIENT); // ✅ Test du rôle

        // ✅ Vérification des valeurs définies
        assertThat(customer.getFirstname()).isEqualTo("Jane");
        assertThat(customer.getLastname()).isEqualTo("Smith");
        assertThat(customer.getEmail()).isEqualTo("jane.smith@example.com");
        assertThat(customer.getPassword()).isEqualTo("password123");
        assertThat(customer.getPhoneNumber()).isEqualTo("0987654321");
        assertThat(customer.isIsActive()).isTrue();
        assertThat(customer.getRole()).isEqualTo(Role.CLIENT); // ✅ Vérification du rôle
    }

    @Test     
    
    public void testCustomerConstructor() {
        // ✅ Création d'un Customer avec le constructeur complet
        Customer customer = new Customer(
            1L, 
            "Jane", 
            "Smith", 
            "jane.smith@example.com", 
            "password123", 
            "123 Main St", 
            "0987654321", 
            Role.CLIENT, 
            true
        );

        // ✅ Vérification des valeurs définies
        assertThat(customer.getId()).isEqualTo(1L);
        assertThat(customer.getFirstname()).isEqualTo("Jane");
        assertThat(customer.getLastname()).isEqualTo("Smith");
        assertThat(customer.getEmail()).isEqualTo("jane.smith@example.com");
        assertThat(customer.getPassword()).isEqualTo("password123");
        assertThat(customer.getAddress()).isEqualTo("123 Main St");
        assertThat(customer.getPhoneNumber()).isEqualTo("0987654321");
        assertThat(customer.getRole()).isEqualTo(Role.CLIENT);
        assertThat(customer.isIsActive()).isTrue();
    }
}
