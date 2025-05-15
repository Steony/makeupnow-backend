package com.makeupnow.backend.repository.mysql;

import com.makeupnow.backend.model.mysql.Customer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Utiliser la config H2 de test
@ActiveProfiles("test") // Utilise le profil de test
@Transactional
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    
    void testSaveCustomer() {
        Customer customer = new Customer();
        customer.setFirstname("John");
        customer.setLastname("Doe");
        customer.setEmail("john.doe@example.com");

        Customer savedCustomer = customerRepository.save(customer);
        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getId()).isNotNull();
    }

    @Test
    
    void testFindCustomerById() {
        Customer customer = new Customer();
        customer.setFirstname("Jane");
        customer.setLastname("Smith");
        customer.setEmail("jane.smith@example.com");

        Customer savedCustomer = customerRepository.save(customer);

        Optional<Customer> foundCustomer = customerRepository.findById(savedCustomer.getId());
        assertThat(foundCustomer).isPresent();
        assertThat(foundCustomer.get().getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Test
    
    void testFindAllCustomers() {
        Customer customer1 = new Customer();
        customer1.setFirstname("Alice");
        customer1.setLastname("Wonderland");
        customer1.setEmail("alice@example.com");

        Customer customer2 = new Customer();
        customer2.setFirstname("Bob");
        customer2.setLastname("Builder");
        customer2.setEmail("bob@example.com");

        customerRepository.save(customer1);
        customerRepository.save(customer2);

        List<Customer> customers = customerRepository.findAll();
        assertThat(customers).hasSize(2);
    }

    @Test
   
    void testDeleteCustomer() {
        Customer customer = new Customer();
        customer.setFirstname("Charlie");
        customer.setLastname("Brown");
        customer.setEmail("charlie.brown@example.com");

        Customer savedCustomer = customerRepository.save(customer);
        customerRepository.deleteById(savedCustomer.getId());

        Optional<Customer> deletedCustomer = customerRepository.findById(savedCustomer.getId());
        assertThat(deletedCustomer).isNotPresent();
    }
}
