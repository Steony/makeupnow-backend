package com.makeupnow.backend.service.mysql;

import com.makeupnow.backend.model.mysql.Customer;
import com.makeupnow.backend.repository.mysql.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }
//Crée une nouvelle entrée  ou mettre à jour une entrée existante
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    // Mise à jour d'un client
   public Customer updateCustomer(Long id, Customer updatedCustomer) {
    Customer existingCustomer = customerRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Customer not found"));
    
    existingCustomer.setFirstname(updatedCustomer.getFirstname());
    existingCustomer.setLastname(updatedCustomer.getLastname());
    existingCustomer.setEmail(updatedCustomer.getEmail());
    existingCustomer.setPassword(updatedCustomer.getPassword());
    existingCustomer.setAddress(updatedCustomer.getAddress());
    existingCustomer.setPhoneNumber(updatedCustomer.getPhoneNumber());
    existingCustomer.setRole(updatedCustomer.getRole());
    existingCustomer.setIsActive(updatedCustomer.isIsActive());

    return customerRepository.save(existingCustomer);
}


// Désactivation / Réactivation
    public void setCustomerActiveStatus(Long id, boolean isActive) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer != null) {
            customer.setIsActive(isActive);
            customerRepository.save(customer);
        }
    }
    
}
